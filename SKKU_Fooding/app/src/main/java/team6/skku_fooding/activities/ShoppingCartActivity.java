package team6.skku_fooding.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import team6.skku_fooding.R;

class CartItem {
    boolean check;
    int count;
    final String item;
    final int price;

    CartItem (String item, int price) {
        this.check = true;
        this.count = 1;
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
            countTextView.setText(Integer.toString(i.count));
            priceTextView.setText(Integer.toString(i.price));
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
                    Integer.toString(cart.stream()
                            .filter(it -> it.check)
                            .map(it -> it.count * it.price)
                            .filter(res -> res > 0)
                            .reduce(Integer::sum).get()));
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

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

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
        shoppingCart.add(new CartItem("참치", 10000));
        shoppingCart.add(new CartItem("꽁치", 8000));
        shoppingCart.add(new CartItem("김치", 4000));
        shoppingCart.add(new CartItem("날치", 12000));

        lm = new LinearLayoutManager(this);
        cad = new CartAdapter(shoppingCart, totalPriceTextView);

        cartRecView.setLayoutManager(lm);
        cartRecView.setAdapter(cad);
        deleteItemButton.setOnClickListener(v -> deleteItem());
        orderAllButton.setOnClickListener(v -> orderAll());
    }

    public void deleteItem() {
        shoppingCart.removeIf(it -> it.check);
        cad.refresh();
    }
    public void orderAll() {
        if (shoppingCart.isEmpty()) {
            Toast.makeText(this, "No items in shopping cart.", Toast.LENGTH_LONG).show();
            return;
        }
    }
}
