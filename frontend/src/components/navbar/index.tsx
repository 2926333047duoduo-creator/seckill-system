import { LeftOutlined } from "@ant-design/icons";
import React from "react";
import { useNavigate } from "react-router-dom";
import styles from "./index.module.scss";

interface NavbarProps {
  title: string;
}

const Navbar: React.FC<NavbarProps> = (props: NavbarProps) => {
  const navigate = useNavigate();

  return (
    <div>
      <div className={styles.navbar}>
        <div className={styles.navbarLeft} onClick={() => navigate(-1)}>
          <LeftOutlined />
        </div>
        <div className={styles.navbarTitle}>{props.title}</div>
      </div>
      <div style={{ height: "7vh" }}></div>
    </div>
  );
};

export default Navbar;
