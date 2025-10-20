import { Ticket } from "lucide-react";
import React, { useEffect, useMemo, useState } from "react";
import { getMyCoupon } from "../../api/client";
import Navbar from "../../components/navbar";
import styles from "./index.module.scss";

interface Coupon {
  id: number;
  name: string;
  amount: string;
  color?: string;
}

const MyCoupon: React.FC = () => {
  const [coupons, setCoupons] = useState<Coupon[]>([]);

  const colorList = useMemo(
    () => [
      "linear-gradient(135deg, #ff6a00, #ee0979)",
      "linear-gradient(135deg, #9C27B0, #E040FB)",
      "linear-gradient(135deg, #03A9F4, #00BCD4)",
      "linear-gradient(135deg, #4CAF50, #009688)",
      "linear-gradient(135deg, #3F51B5, #9C27B0)",
      "linear-gradient(135deg, #FFC107, #FF9800)",
    ],
    []
  );

  useEffect(() => {
    const fetchCoupons = async () => {
      const res = await getMyCoupon();
      if (res.code === 200) {
        const data = ((res.data as Coupon[]) || []).map(
          (c: Coupon, i: number) => ({
            ...c,
            color: c.color || colorList[i % colorList.length],
          })
        );
        setCoupons(data);
      }
    };
    fetchCoupons();
  }, [colorList]);

  return (
    <div className={styles.page}>
      <Navbar title="My Coupons" />
      <div className={styles.header}>
        <div className={styles.headerInner}>
          <div className={styles.iconBox}>
            <Ticket className={styles.ticketIcon} />
          </div>
          <div>
            <h1 className={styles.title}>My Coupons</h1>
            <p className={styles.subtitle}>
              You have {coupons.length} coupon{coupons.length !== 1 ? "s" : ""}
            </p>
          </div>
        </div>
      </div>

      <div className={styles.listContainer}>
        {coupons.map((coupon) => (
          <div key={coupon.id} className={styles.card}>
            <div className={styles.left} style={{ background: coupon.color }}>
              <div className={styles.amount}>${coupon.amount}</div>
              <div className={styles.dashed}></div>
            </div>

            <div className={styles.right}>
              <h3 className={styles.couponTitle}>{coupon.name}</h3>
            </div>
          </div>
        ))}

        {coupons.length === 0 && (
          <div className={styles.empty}>No coupons available</div>
        )}
      </div>
    </div>
  );
};

export default MyCoupon;
