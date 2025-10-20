import { RightOutlined } from "@ant-design/icons";
import { message, Modal } from "antd";
import Cookies from "js-cookie";
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./index.module.scss";

const allFuncLists: string[] = [
  "My Account",
  "My Orders",
  "My Coupons",
  "Set Voucher",
  "My Feedback",
  "FAQ",
  "Privacy Policy",
  "Contact Us",
  "Settings",
  "Log out",
];

const Profile: React.FC = () => {
  const navigate = useNavigate();
  const [messageApi, contextHolder] = message.useMessage();
  const [isModalOpen, setIsModalOpen] = useState(false);

  // read role from Cookie
  const role = Cookies.get("role"); // "ADMIN" or "USER"
  const username = Cookies.get("username");
  const token = Cookies.get("token");
  const isLogin = Boolean(username && token);

  const funcLists = (() => {
    let funcLists = [...allFuncLists];
    if (role === "CLIENT") {
      // if CLIENT, remove "Set Voucher"
      funcLists = funcLists.filter((item) => item !== "Set Voucher");
    }
    if (role === "ADMIN") {
      // if ADMIN, remove "My Coupons"
      funcLists = funcLists.filter((item) => item !== "My Coupons");
    }
    if (!isLogin) {
      // if not login, remove "Log out"
      funcLists = funcLists.filter((item) => item !== "Log out");
    }
    return funcLists;
  })();

  const handleOk = () => {
    Cookies.remove("token", { path: "/" });
    Cookies.remove("role", { path: "/" });
    Cookies.remove("username", { path: "/" });
    Cookies.remove("account", { path: "/" });
    setIsModalOpen(false);
    navigate("/login");
  };

  const itemClickHandler = (item: string) => {
    if (item === "My Coupons") {
      navigate("/me/myCoupon");
    } else if (item === "Set Voucher") {
      navigate("/me/couponList");
    } else if (item === "Log out") {
      setIsModalOpen(true);
    } else {
      messageApi.info("It doesn't seem to be working");
    }
  };

  return (
    <div className={styles["me-page"]}>
      {contextHolder}
      <div className={styles["me-bg-image"]}></div>
      <Modal
        title="Log out"
        closable={false}
        open={isModalOpen}
        onOk={handleOk}
        onCancel={() => setIsModalOpen(false)}
        okText="Log out"
        cancelText="Cancel"
        width={"80vw"}
        style={{ marginTop: "24vh" }}
      >
        <p>Are you sure you want to log out? </p>
      </Modal>
      <div className={styles["me-title"]}>
        {isLogin ? (
          <div>
            Welcome,
            <br />
            <div className={styles["username"]}>{username}</div>
          </div>
        ) : (
          <div>
            Guest
            <br />
            Please{" "}
            <span
              style={{
                color: "#1890ff",
                textDecoration: "underline",
                cursor: "pointer",
              }}
              onClick={() => navigate("/login")}
            >
              Login
            </span>
          </div>
        )}
      </div>
      <div className={styles["func-list"]}>
        {funcLists.map((item) => (
          <div
            className={styles["func-item"]}
            key={item}
            onClick={() => itemClickHandler(item)}
          >
            <span>{item}</span>
            <span>
              <RightOutlined />
            </span>
          </div>
        ))}
      </div>
      <div style={{ height: "12vh" }}></div>
    </div>
  );
};

export default Profile;
