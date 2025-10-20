import { message } from "antd";
import { Check, ChevronRight, Clock, Gift } from "lucide-react";
import React, { useCallback, useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  clientSeckill,
  getClientCouponList,
  getMyCoupon,
} from "../../api/client";
import Navbar from "../../components/navbar";
import type { AnyType } from "../../types";
import styles from "./index.module.scss";

const UserCouponGrab: React.FC = () => {
  const navigate = useNavigate();
  const [messageApi, contextHolder] = message.useMessage();

  const [coupons, setCoupons] = useState<AnyType[]>([]);
  const [showSuccessModal, setShowSuccessModal] = useState(false);

  const colorList = useMemo(() => {
    return [
      "linear-gradient(135deg, #ff6a00, #ee0979)",
      "linear-gradient(135deg, #9C27B0, #E040FB)",
      "linear-gradient(135deg, #03A9F4, #00BCD4)",
      "linear-gradient(135deg, #4CAF50, #009688)",
      "linear-gradient(135deg, #FF9800, #F44336)",
    ];
  }, []);

  const fetchCoupons = useCallback(async () => {
    try {
      const [allCouponRes, myCouponRes] = await Promise.all([
        getClientCouponList(),
        getMyCoupon(),
      ]);
      if (allCouponRes.code !== 200) {
        messageApi.error("Failed to fetch coupon list");
        return;
      }
      const myIds = (myCouponRes?.data || []).map((item: AnyType) => item.id);
      const colored = (allCouponRes.data as object[]).map(
        (c: AnyType, i: number) => ({
          ...c,
          color: colorList[i % colorList.length],
          grabbed: myIds.includes(c.id),
        })
      );
      setCoupons(colored);
    } catch (err) {
      console.error(err);
      messageApi.error("Failed to load coupons");
    }
  }, [colorList, messageApi]);

  useEffect(() => {
    fetchCoupons();
  }, [fetchCoupons]);

  const handleGrab = async (coupon: AnyType) => {
    const now = Date.now();
    const start = new Date(coupon.startTime).getTime();
    if (now < start) {
      messageApi.warning("The event has not started yet!");
      return;
    }
    if (coupon.grabbed || coupon.stock <= 0) return;
    const res = await clientSeckill(coupon.id);
    if (res.code !== 200) {
      messageApi.error(res.msg || "Fail to grab coupon");
      return;
    }
    setCoupons((prev) =>
      prev.map((c) =>
        c.id === coupon.id
          ? { ...c, grabbed: true, stock: Math.max(0, c.stock - 1) }
          : c
      )
    );
    setShowSuccessModal(true);
    setTimeout(() => setShowSuccessModal(false), 1800);
  };

  return (
    <div className={styles.page}>
      {contextHolder}
      <Navbar title="Coupon Center" />

      {/* Header */}
      <div className={styles.header}>
        <div className={styles.headerInner}>
          <div className={styles.headerIcon}>
            <Gift className={styles.giftIcon} />
          </div>
          <div>
            <h1 className={styles.title}>Coupon Center</h1>
            <p className={styles.subtitle}>
              Limited-time offers, grab yours now!
            </p>
          </div>
        </div>
      </div>

      {/* Coupon List */}
      <div className={styles.listContainer}>
        {coupons.map((coupon) => {
          const total = coupon.total;
          const stock = coupon.stock;
          const progress = ((total - stock) / total) * 100;
          const isHot = stock < 10;
          const notStarted = new Date() < new Date(coupon.startTime);

          return (
            <div key={coupon.id} className={styles.card}>
              {/* Left */}
              <div
                className={styles.cardLeft}
                style={{ background: coupon.color }}
              >
                <div className={styles.amount}>¥{coupon.amount}</div>
                <div className={styles.condition}>Voucher</div>
                {isHot && !coupon.grabbed && (
                  <div className={styles.hot}>🔥 Almost gone</div>
                )}
              </div>

              {/* Right */}
              <div className={styles.cardRight}>
                <div className={styles.cardHeader}>
                  <h3>{coupon.name}</h3>
                  <button
                    onClick={() => handleGrab(coupon)}
                    disabled={coupon.grabbed || stock <= 0 || notStarted}
                    className={`${styles.grabBtn} ${
                      coupon.grabbed || stock <= 0 || notStarted
                        ? styles.disabled
                        : ""
                    }`}
                  >
                    {notStarted ? (
                      "Not started"
                    ) : coupon.grabbed ? (
                      <>
                        <Check className={styles.smallIcon} />
                        Claimed
                      </>
                    ) : stock <= 0 ? (
                      "Sold out"
                    ) : (
                      "Grab Now"
                    )}
                  </button>
                </div>

                <div className={styles.cardBody}>
                  <div className={styles.dateRow}>
                    <Clock className={styles.smallIcon} />
                    <span>
                      Starts on{" "}
                      {new Date(coupon.startTime).toLocaleString("en-GB", {
                        hour12: false,
                        year: "numeric",
                        month: "2-digit",
                        day: "2-digit",
                        hour: "2-digit",
                        minute: "2-digit",
                      })}
                    </span>
                  </div>

                  <div className={styles.progressBox}>
                    <div className={styles.progressTop}>
                      <span>Claimed {total - stock}</span>
                      <span
                        className={
                          stock < 10 ? styles.stockHot : styles.stockNormal
                        }
                      >
                        Remaining {stock}
                      </span>
                    </div>
                    <div className={styles.progressBar}>
                      <div
                        className={styles.progress}
                        style={{
                          width: `${progress}%`,
                          background:
                            progress > 80
                              ? "#f44336"
                              : "linear-gradient(to right, #ff9800, #f44336)",
                        }}
                      ></div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          );
        })}

        {/* My Coupons */}
        <div
          className={styles.myCouponBtn}
          onClick={() => navigate("/me/myCoupon")}
        >
          <ChevronRight className={styles.arrow} />
          View My Coupons
        </div>
      </div>

      {/* Success Modal */}
      {showSuccessModal && (
        <div className={styles.modalOverlay}>
          <div className={styles.modal}>
            <div className={styles.modalIcon}>
              <Check className={styles.modalCheck} />
            </div>
            <h3>Coupon Claimed!</h3>
          </div>
        </div>
      )}
    </div>
  );
};

export default UserCouponGrab;
