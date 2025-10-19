import React from "react";
import styles from "./index.module.scss";

const Cart: React.FC = () => {
  return (
    <div className={styles["cart-page"]}>
      <div className={styles["cart-empty"]}>
        <div className={styles["cart-empty-image"]}></div>
        Emm...
        <br />
        Your cart is empty~
      </div>
    </div>
  );
};

export default Cart;
