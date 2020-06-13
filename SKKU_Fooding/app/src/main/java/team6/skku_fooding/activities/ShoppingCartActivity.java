package team6.skku_fooding.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import team6.skku_fooding.R;
import team6.skku_fooding.models.Product;

class CartItem {
    boolean check;
    int count;
    final int pid;
    final String item;
    final int price;

    CartItem (int pid, String item, int price) {
        this.check = true;
        this.count = 1;
        this.pid = pid;
        this.item = item;
        this.price = price;
    }
    void plus() { count++; }
    void minus() { count--; if (count < 1) count = 1; }
    void check() { check = !check; }
}

@SuppressLint("SetTextI18n")
@RequiresApi(api = Build.VERSION_CODES.N)
class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartItemViewHolder> {
    private ArrayList<CartItem> cart;
    private TextView totalPriceTextView;

    static class CartItemViewHolder extends RecyclerView.ViewHolder {
        CheckBox check;
        TextView itemTitle;
        ImageButton minusButton;
        ImageButton plusButton;
        TextView countTextView;
        TextView priceTextView;

        CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            check = itemView.findViewById(R.id.checkBox);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            minusButton = itemView.findViewById(R.id.minusCountButton);
            plusButton = itemView.findViewById(R.id.plusCountButton);
            countTextView = itemView.findViewById(R.id.countTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
        }
        void bind(CartItem i) {
            itemTitle.setText(i.item);
            countTextView.setText(String.valueOf(i.count));
            priceTextView.setText(String.valueOf(i.price+"₩"));
            check.setChecked(i.check);
        }
    }

    CartAdapter(ArrayList<CartItem> cart, TextView totalPriceTextView) {
        this.cart = cart;
        this.totalPriceTextView = totalPriceTextView;
        refresh();
    }
    @NonNull @Override public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartItemViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.cart_item, parent, false));
    }
    @Override public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem it = cart.get(position);
        holder.check.setOnClickListener(v -> { it.check(); refresh(); });
        holder.minusButton.setOnClickListener(v -> { it.minus(); refresh(); });
        holder.plusButton.setOnClickListener(v -> { it.plus(); refresh(); });
        holder.bind(it);
    }
    @Override public int getItemCount() { return cart.size(); }
    public void refresh() {
        try {
            totalPriceTextView.setText("Total price: "+
                    String.valueOf(cart.stream()
                            .filter(it -> it.check)
                            .map(it -> it.count * it.price)
                            .filter(res -> res > 0)
                            .reduce(Integer::sum).get())+"₩");
        } catch (NoSuchElementException e) {
            totalPriceTextView.setText("No items selected.");
        }
        this.notifyDataSetChanged();
    }
}

@SuppressLint("SetTextI18n")
@RequiresApi(api = Build.VERSION_CODES.N)
public class ShoppingCartActivity extends AppCompatActivity {
    private ArrayList<CartItem> shoppingCart;
    private RecyclerView cartRecView;
    private TextView totalPriceTextView;
    private CartAdapter cad;
    private RecyclerView.LayoutManager lm;
    private Button deleteItemButton;
    private Button orderAllButton;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private DatabaseReference productRef;
    private SharedPreferences loginPref;
    private SharedPreferences.Editor editor;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        loginPref = getSharedPreferences("user_SP", MODE_PRIVATE);
        editor = loginPref.edit();

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userRef = db.getReference("user");
        productRef = db.getReference("product");

        cartRecView = findViewById(R.id.cartRecyclerView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        deleteItemButton = findViewById(R.id.deleteItemButton);
        orderAllButton = findViewById(R.id.orderAllButton);
        cartRecView.setHasFixedSize(true);

        shoppingCart = new ArrayList<>();

        lm = new LinearLayoutManager(this);
        cad = new CartAdapter(shoppingCart, totalPriceTextView);

        cartRecView.setLayoutManager(lm);
        cartRecView.setAdapter(cad);
        deleteItemButton.setOnClickListener(v -> deleteItem());
        orderAllButton.setOnClickListener(v -> orderAll());

        productRef.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot ds) {
                if (ds.exists()) {
                    HashMap<String, Object> res = ds.getValue(new GenericTypeIndicator<HashMap<String, Object>>() {});
                    if (res != null) {
                        for (String s : loginPref.getString("cart_item", "").split("-")) {
                            try {
                                String[] as = s.split(":");
                                int pid = Integer.parseInt(as[0]);
                                int cnt = Integer.parseInt(as[1]);
                                int price = Integer.parseInt(as[2]);
                                String name = res.entrySet().stream().filter(it -> Integer.parseInt(it.getKey()) == pid)
                                        .map(it -> (String)((HashMap<String, Object>)it.getValue()).get("name")).findFirst().get();
                                CartItem c = new CartItem(pid, name, price);
                                c.count = cnt;
                                shoppingCart.add(c);
                            } catch (IllegalArgumentException | NoSuchElementException e) {
                                e.printStackTrace();
                            }
                        }
                        cad.refresh();
                    }
                }
                Log.d("ShoppingCartActivity", "Product query success.");
            }

            @Override public void onCancelled(@NonNull DatabaseError de) { Log.w("ShoppingCartActivity", "Product query cancelled."); }
        });
    }
    @Override protected void onStop() {
        super.onStop();
        String uid = loginPref.getString("UID", null);
        if (uid != null) userRef.child(uid).child("shopping_cart").setValue(loginPref.getString("cart_item", ""));
    }

    public void deleteItem() {
        shoppingCart.removeIf(it -> it.check);
        editor.putString("cart_item", serializer(shoppingCart)).apply();
        cad.refresh();
    }
    public void orderAll() {
        if (shoppingCart.isEmpty()) {
            Toast.makeText(this, "No items in shopping cart.", Toast.LENGTH_LONG).show();
            return;
        }
        startActivity(new Intent(this, OrderActivity.class)
                .putExtra("sending_item", serializer(shoppingCart)));
        this.finish();
    }
    // Unfortunately, firebase query is asynchronous, so I can't make deserializer separately.

    public static String serializer(ArrayList<CartItem> ac) {
        try {
            return ac.stream().map(c -> c.pid+":"+c.count+":"+c.price).reduce((x, y) -> x+"-"+y).get();
        } catch (NoSuchElementException e) {
            return "";
        }
    }
}
