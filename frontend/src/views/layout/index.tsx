import React from "react";
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import styles from "./index.module.scss";

const Layout: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const currentPath = location.pathname;

  return (
    <div>
      <Outlet />
      <div className={styles["footer-navigator"]}>
        <div
          className={styles["footer-navigator-image-box"]}
          onClick={() => navigate("/shop")}
        >
          <div
            className={`${styles["footer-navigator-shop"]} ${
              currentPath === "/shop" ? styles["active-shop"] : ""
            }`}
          ></div>
          <div
            className={`${styles["footer-navigator-text"]} ${
              currentPath === "/shop" ? styles["active-text"] : ""
            }`}
          >
            Shop
          </div>
        </div>

        <div
          className={styles["footer-navigator-image-box"]}
          onClick={() => navigate("/cart")}
        >
          <div
            className={`${styles["footer-navigator-cart"]} ${
              currentPath === "/cart" ? styles["active-cart"] : ""
            }`}
          ></div>
          <div
            className={`${styles["footer-navigator-text"]} ${
              currentPath === "/cart" ? styles["active-text"] : ""
            }`}
          >
            Cart
          </div>
        </div>

        <div
          className={styles["footer-navigator-image-box"]}
          onClick={() => navigate("/me")}
        >
          <div
            className={`${styles["footer-navigator-me"]} ${
              currentPath.startsWith("/me/") ? styles["active-me"] : ""
            }`}
          ></div>
          <div
            className={`${styles["footer-navigator-text"]} ${
              currentPath.startsWith("/me/") ? styles["active-text"] : ""
            }`}
          >
            Me
          </div>
        </div>
      </div>
    </div>
  );
};

export default Layout;
