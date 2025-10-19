import { RightOutlined } from "@ant-design/icons";
import { message, Modal } from "antd";
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./index.module.scss";

const allFuncLists: string[] = [
  "My Account",
  "My Orders",
  "My Voucher",
  "Set Voucher",
  "My Feedback",
  "FAQ",
  "Privacy Policy",
  "Contact Us",
  "Settings",
  "Log out",
];

const Me: React.FC = () => {
  const navigate = useNavigate();
  const [messageApi, contextHolder] = message.useMessage();
  const [isModalOpen, setIsModalOpen] = useState(false);

  // read role from localStorage
  const role = localStorage.getItem("role"); // "ADMIN" or "USER"

  // if not "ADMIN", remove "Set Voucher"
  const funcLists =
    role === "ADMIN"
      ? allFuncLists
      : allFuncLists.filter((item) => item !== "Set Voucher");

  const handleOk = () => {
    setIsModalOpen(false);
    navigate("/login");
  };

  const itemClickHandler = (item: string) => {
    if (item === "My Voucher") {
      console.log("clicked My Voucher");
    } else if (item === "Set Voucher") {
      console.log("clicked Set Voucher");
    } else if (item === "Log out") {
      setIsModalOpen(true);
    } else {
      messageApi.info("It doesn't seem to be working");
    }
  };

  return (
    <div className={styles["me-page"]}>
      {contextHolder}
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
        Welcome, <div>username</div>
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

export default Me;
