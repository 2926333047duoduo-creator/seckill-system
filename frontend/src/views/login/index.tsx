import { CloseOutlined } from "@ant-design/icons";
import type { CheckboxChangeEvent } from "antd";
import { Checkbox, message, Segmented } from "antd";
import Cookies from "js-cookie";
import React from "react";
import { useNavigate } from "react-router-dom";
import { authLogin, authRegister } from "../../api/auth";
import styles from "./index.module.scss";

const Login: React.FC = () => {
  const navigate = useNavigate();
  const [messageApi, contextHolder] = message.useMessage();

  const [pageType, setPageType] = React.useState<"login" | "register">("login");
  const [username, setUsername] = React.useState("");
  const [role, setRole] = React.useState<"ADMIN" | "CLIENT">("CLIENT");
  const [account, setAccount] = React.useState("");
  const [password, setPassword] = React.useState("");
  const [agreement_checked, setAgreement_checked] = React.useState(false);

  const onChange = (e: CheckboxChangeEvent) => {
    setAgreement_checked(e.target.checked);
  };

  // handle login logic
  const loginHandler = async () => {
    if (account.trim() === "" || password.trim() === "") {
      messageApi.warning("Please fill in all the required fields");
      return;
    }
    if (!agreement_checked) {
      messageApi.warning("Please accept the user agreements first");
      return;
    }
    const res = await authLogin({
      role,
      account,
      password,
    });
    if (res.code === 200) {
      const token = res.data;
      // set Cookies
      Cookies.set("token", token, { expires: 7, path: "/" });
      Cookies.set("role", role, { expires: 7, path: "/" });
      Cookies.set("username", username, { expires: 7, path: "/" });
      Cookies.set("account", account, { expires: 7, path: "/" });
      messageApi.success("Login successfully");
      navigate("/shop");
    } else {
      messageApi.error(res.msg);
    }
  };

  // handle register logic
  const registerHandler = async () => {
    if (
      username.trim() === "" ||
      account.trim() === "" ||
      password.trim() === ""
    ) {
      messageApi.warning("Please fill in all the required fields");
      return;
    }
    const data = {
      username,
      account,
      password,
    };
    const res = await authRegister(data);
    if (res.code === 200) {
      messageApi.success("Register successfully");
      setUsername("");
      setAccount("");
      setPassword("");
      setPageType("login");
    } else {
      messageApi.error(res.msg);
    }
  };

  return (
    <div className={styles["login-page"]}>
      {contextHolder}
      <div className={styles["login-header"]} onClick={() => navigate(-1)}>
        <CloseOutlined />
      </div>
      <div className={styles["welcome"]}>Welcome to seckill</div>
      <div className={styles["input-container"]}>
        {pageType === "register" ? (
          <input
            className={styles["login-input"]}
            placeholder="Please enter your username"
            defaultValue={username}
            onChange={(e) => setUsername(e.target.value)}
          />
        ) : (
          <div className={styles["login-role"]}>
            <span>Select Your Role</span>
            <span style={{ marginLeft: "4vw" }}>
              <Segmented<string>
                options={["admin", "client"]}
                defaultValue={role.toLowerCase()}
                onChange={(value) => {
                  setRole(value.toUpperCase() as "ADMIN" | "CLIENT");
                }}
              />
            </span>
          </div>
        )}
        <input
          className={styles["login-input"]}
          placeholder="Please enter your account"
          defaultValue={account}
          onChange={(e) => setAccount(e.target.value)}
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
            onClick={() => messageApi.info("It doesn't seem to be working")}
          >
            the user agreements
          </span>
        </span>
        <div
          className={styles["login-btn"]}
          onClick={() =>
            pageType === "login" ? loginHandler() : registerHandler()
          }
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
          onClick={() => messageApi.info("It doesn't seem to be working")}
        ></div>
        <div
          className={styles["login-google"]}
          onClick={() => messageApi.info("It doesn't seem to be working")}
        ></div>
        <div
          className={styles["login-tiktok"]}
          onClick={() => messageApi.info("It doesn't seem to be working")}
        ></div>
      </div>
    </div>
  );
};

export default Login;
