import { Check, ChevronRight, Clock, Gift } from "lucide-react";
import React, { useEffect, useState } from "react";
import { getCouponList } from "../../api/admin";
import { clientSeckill } from "../../api/client";
import Navbar from "../../components/navbar";
import type { AnyType } from "../../types";
import styles from "./index.module.scss";

const UserCouponGrab: React.FC = () => {
  const [coupons, setCoupons] = useState<AnyType[]>([]);
  const [showSuccessModal, setShowSuccessModal] = useState(false);

  const fetchCoupons = async () => {
    const res = await getCouponList();
    if (res.code === 200) {
      // add color and grabbed fields for display
      const colored = (res.data as object[]).map((c: AnyType, i: number) => ({
        ...c,
        color: colorList[i % colorList.length],
        grabbed: false,
      }));
      setCoupons(colored);
    }
  };

  useEffect(() => {
    fetchCoupons();
  }, []);

  const colorList = [
    "linear-gradient(135deg, #ff6a00, #ee0979)",
    "linear-gradient(135deg, #9C27B0, #E040FB)",
    "linear-gradient(135deg, #03A9F4, #00BCD4)",
    "linear-gradient(135deg, #4CAF50, #009688)",
    "linear-gradient(135deg, #FF9800, #F44336)",
  ];

  const handleGrab = async (coupon: AnyType) => {
    if (coupon.grabbed || coupon.stock <= 0) return;
    const res = await clientSeckill(coupon.id);
    if (res.code === 200) {
      coupon.grabbed = true;
    }
    fetchCoupons();
    setShowSuccessModal(true);
    setTimeout(() => setShowSuccessModal(false), 1800);
  };

  return (
    <div className={styles.page}>
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

          return (
            <div key={coupon.id} className={styles.card}>
              {/* Left side - coupon visual */}
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

              {/* Right side - details */}
              <div className={styles.cardRight}>
                <div className={styles.cardHeader}>
                  <h3>{coupon.name}</h3>
                  <button
                    onClick={() => handleGrab(coupon)}
                    disabled={coupon.grabbed || stock <= 0}
                    className={`${styles.grabBtn} ${
                      coupon.grabbed || stock <= 0 ? styles.disabled : ""
                    }`}
                  >
                    {coupon.grabbed ? (
                      <>
                        <Check className={styles.smallIcon} />
                        Claimed
                      </>
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
                      {new Date(coupon.startTime).toLocaleDateString("en-GB")}
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
        <div className={styles.myCouponBtn}>
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
