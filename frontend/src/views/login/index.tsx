import type { CheckboxChangeEvent } from "antd";
import { Checkbox, message } from "antd";
import React from "react";
import { useNavigate } from "react-router-dom";
import styles from "./index.module.scss";

const Login: React.FC = () => {
  const navigate = useNavigate();
  const [messageApi, contextHolder] = message.useMessage();

  const [pageType, setPageType] = React.useState<"login" | "register">("login");
  const [username, setUsername] = React.useState("");
  const [password, setPassword] = React.useState("");
  const [agreement_checked, setAgreement_checked] = React.useState(false);

  const onChange = (e: CheckboxChangeEvent) => {
    setAgreement_checked(e.target.checked);
  };

  // handle login logic
  const loginHandler = () => {
    if (!agreement_checked) {
      messageApi.warning("Please accept the user agreements first");
      return;
    }
    console.log("Logging in with", { username, password });
    navigate("/shop");
  };

  return (
    <div className={styles["login-page"]}>
      {contextHolder}
      <div className={styles["welcome"]}>Welcome to seckill</div>
      <div className={styles["input-container"]}>
        <input
          className={styles["login-input"]}
          placeholder="Please enter your username"
          defaultValue={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <input
          className={styles["login-input"]}
          placeholder="Please enter your password"
          defaultValue={password}
          onChange={(e) => setPassword(e.target.value)}
        />
      </div>
      <div className={styles["login-container"]}>
        <Checkbox
          onChange={onChange}
          checked={agreement_checked}
          style={{ marginLeft: "6.5vw" }}
        />
        <span style={{ marginLeft: "2vw", fontSize: "4vw" }}>
          I accept{" "}
          <span
            style={{ color: "#1890ff" }}
            onClick={() => messageApi.info("Sorry, not available yet")}
          >
            the user agreements
          </span>
        </span>
        <div
          className={styles["login-btn"]}
          onClick={() => (pageType === "login" ? loginHandler() : null)}
        >
          {pageType === "login" ? "Login" : "Register"}
        </div>
        <div className={styles["exchange-btn"]}>
          {pageType === "login"
            ? "do not have an account? "
            : "already have an account? "}
          <span
            style={{ color: "#1890ff" }}
            onClick={() => {
              if (pageType === "register") {
                setPageType("login");
              } else {
                setPageType("register");
              }
            }}
          >
            {pageType === "login" ? "register" : "login"}
          </span>
        </div>
      </div>
      <div className={styles["login-footer"]}>
        <div
          className={styles["login-facebook"]}
          onClick={() => messageApi.info("Sorry, not available yet")}
        ></div>
        <div
          className={styles["login-google"]}
          onClick={() => messageApi.info("Sorry, not available yet")}
        ></div>
        <div
          className={styles["login-tiktok"]}
          onClick={() => messageApi.info("Sorry, not available yet")}
        ></div>
      </div>
    </div>
  );
};

export default Login;
