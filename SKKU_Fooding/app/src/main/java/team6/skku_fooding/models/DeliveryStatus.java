package team6.skku_fooding.models;

enum DeliveryStatus {
    GONE {
        String value() { return "gone"; }
        String detail() { return "Delivery is missing. Sorry for inconvenient."; }
    },
    UNKNOWN {
        String value() { return "unknown"; }
        String detail() { return "Delivery status is unknown.."; }
    },
    PREPARE {
        String value() { return "prepare"; }
        String detail() { return "We are preparing your delivery."; }
    },
    SENT {
        String value() { return "sent"; }
        String detail() { return "We sent your delivery."; }
    },
    ARRIVED {
        String value() { return "arrived"; }
        String detail() { return "You've got the delivery."; }
    };

    abstract String value();
    abstract String detail();
}
