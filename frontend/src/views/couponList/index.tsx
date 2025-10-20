import { DatePicker, message } from "antd";
import locale from "antd/es/date-picker/locale/en_GB";
import { Calendar, Edit, Package, Plus, Tag, Trash2 } from "lucide-react";
import React, { useCallback, useEffect, useState } from "react";
import {
  addCoupon,
  deleteCoupon,
  getCouponList,
  updateCoupon,
} from "../../api/admin";
import Navbar from "../../components/navbar";
import type { AnyType } from "../../types";
import styles from "./index.module.scss";

const CouponList: React.FC = () => {
  const [messageApi, contextHolder] = message.useMessage();

  const [coupons, setCoupons] = useState<AnyType[]>([]);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [deleteId, setDeleteId] = useState<string | null>(null);
  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [editCoupon, setEditCoupon] = useState<AnyType | null>(null);

  // Add voucher form data
  const [newCoupon, setNewCoupon] = useState({
    name: "",
    amount: "",
    total: "",
    stock: "",
    startDate: "",
    startTime: "",
  });

  const fetchCoupons = useCallback(async () => {
    const res = await getCouponList();
    if (res.code === 200) {
      setCoupons(res.data as object[]);
    } else {
      messageApi.error(res.msg);
    }
  }, [messageApi]);

  useEffect(() => {
    fetchCoupons();
  }, [fetchCoupons]);

  // Prevent page scrolling when popups open
  useEffect(() => {
    const hasModal = showAddModal || showDeleteModal || showEditModal;
    if (hasModal) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "auto";
    }
    return () => {
      document.body.style.overflow = "auto";
    };
  }, [showAddModal, showDeleteModal, showEditModal]);

  const deleteHandler = (id: string) => {
    setDeleteId(id);
    setShowDeleteModal(true);
  };

  const confirmDelete = async () => {
    const res = await deleteCoupon(deleteId as string);
    if (res.code !== 200) {
      messageApi.error(res.msg);
      return;
    }
    fetchCoupons();
    setShowDeleteModal(false);
    setDeleteId(null);
  };

  const confirmAdd = async () => {
    const { name, amount, total, stock, startDate, startTime } = newCoupon;
    if (!name || !amount || !total || !stock || !startDate || !startTime) {
      messageApi.warning("Please fill in all fields.");
      return;
    }
    const combinedTime = `${startDate}T${startTime}:00`;
    const newItem = {
      name,
      amount: parseFloat(amount),
      total: parseInt(total),
      stock: parseInt(stock),
      startTime: combinedTime,
    };
    const res = await addCoupon(newItem);
    if (res.code === 200) {
      messageApi.success("Coupon added successfully.");
      fetchCoupons();
      setShowAddModal(false);
    } else {
      messageApi.error(res.msg);
    }
    setNewCoupon({
      name: "",
      amount: "",
      total: "",
      stock: "",
      startDate: "",
      startTime: "",
    });
  };

  const confirmUpdate = async () => {
    if (!editCoupon) return;
    const { id, name, stock, startDate, startTime } = editCoupon;
    if (!name || !stock || !startDate || !startTime) {
      messageApi.warning("Please fill in all fields.");
      return;
    }
    const combinedTime = `${startDate}T${startTime}:00`;
    const payload = {
      id,
      name,
      stock: parseInt(stock),
      startTime: combinedTime,
    };
    const res = await updateCoupon(payload);
    if (res.code === 200) {
      messageApi.success("Coupon updated successfully.");
      setShowEditModal(false);
      setEditCoupon(null);
      fetchCoupons();
    } else {
      messageApi.error(res.msg);
    }
  };

  return (
    <div className={styles.page}>
      {contextHolder}
      <Navbar title="Coupon Management" />
      <div className={styles.container}>
        {/* Header */}
        <div className={styles.header}>
          <div className={styles.headerLeft}>
            <div className={styles.headerIcon}>
              <Tag className={styles.icon} />
            </div>
            <div>
              <h1 className={styles.title}>Coupon Management</h1>
              <p className={styles.subtitle}>Total {coupons.length} Coupons</p>
            </div>
          </div>
          <button
            className={styles.addButton}
            onClick={() => setShowAddModal(true)}
          >
            <Plus className={styles.addIcon} />
            Add Coupon
          </button>
        </div>

        {/* Coupon Cards */}
        <div className={styles.grid}>
          {coupons.map((coupon) => (
            <div key={coupon.id} className={styles.card}>
              <div className={styles.cardHeader}>
                <div>
                  <h3 className={styles.cardTitle}>{coupon.name}</h3>
                  <div className={styles.status}>Available</div>
                </div>
                <div className={styles.discountBox}>
                  <div className={styles.discount}>¥{coupon.amount}</div>
                  <div className={styles.condition}>
                    Total {coupon.total} • Stock {coupon.stock}
                  </div>
                </div>
              </div>

              <div className={styles.cardBody}>
                <div className={styles.usage}>
                  <div className={styles.usageTop}>
                    <div className={styles.usageLabel}>
                      <Package className={styles.smallIcon} />
                      <span>Stock</span>
                    </div>
                    <span className={styles.usageNum}>
                      {coupon.stock}/{coupon.total} (
                      {Math.round((coupon.stock / coupon.total) * 100)}%)
                    </span>
                  </div>
                  <div className={styles.usageBar}>
                    <div
                      className={styles.usageProgress}
                      style={{
                        width: `${Math.round(
                          (coupon.stock / coupon.total) * 100
                        )}%`,
                      }}
                    ></div>
                  </div>
                </div>

                <div className={styles.date}>
                  <Calendar className={styles.smallIcon} />
                  <span>
                    Start:{" "}
                    {new Date(coupon.startTime).toLocaleString("en-GB", {
                      dateStyle: "short",
                      timeStyle: "short",
                    })}{" "}
                    <br />
                    Created:{" "}
                    {new Date(coupon.createTime).toLocaleDateString("en-GB")}
                  </span>
                </div>

                <div className={styles.infoGrid}>
                  <div>
                    <div className={styles.infoLabel}>Amount</div>
                    <div className={styles.infoValue}>¥{coupon.amount}</div>
                  </div>
                  <div>
                    <div className={styles.infoLabel}>Remaining</div>
                    <div className={styles.infoValue}>
                      {coupon.stock} / {coupon.total}
                    </div>
                  </div>
                </div>

                <div className={styles.actions}>
                  <button
                    className={styles.editBtn}
                    onClick={() => {
                      const [datePart, timePart] = coupon.startTime.split("T");
                      setEditCoupon({
                        ...coupon,
                        startDate: datePart,
                        startTime: timePart.slice(0, 5),
                      });
                      setShowEditModal(true);
                    }}
                  >
                    <Edit className={styles.smallIcon} />
                    Edit
                  </button>
                  <button
                    onClick={() => deleteHandler(coupon.id)}
                    className={styles.deleteBtn}
                  >
                    <Trash2 className={styles.smallIcon} />
                    Delete
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>

        {coupons.length === 0 && (
          <div className={styles.empty}>
            <div className={styles.emptyIcon}>
              <Tag className={styles.iconLarge} />
            </div>
            <h3>No Coupons Yet</h3>
            <p>Click the button above to create your first coupon.</p>
          </div>
        )}
      </div>

      {/* Add Coupon Modal */}
      {showAddModal && (
        <div className={styles.modalOverlay}>
          <div className={styles.modal}>
            <h3 style={{ marginBottom: "2vh" }}>Add New Coupon</h3>
            <div className={styles.formGroup}>
              <input
                type="text"
                placeholder="Coupon Name"
                value={newCoupon.name}
                onChange={(e) =>
                  setNewCoupon({ ...newCoupon, name: e.target.value })
                }
              />
              <input
                type="number"
                placeholder="Amount"
                value={newCoupon.amount}
                onChange={(e) =>
                  setNewCoupon({ ...newCoupon, amount: e.target.value })
                }
              />
              <input
                type="number"
                placeholder="Total Quantity"
                value={newCoupon.total}
                onChange={(e) =>
                  setNewCoupon({ ...newCoupon, total: e.target.value })
                }
              />
              <input
                type="number"
                placeholder="Stock"
                value={newCoupon.stock}
                onChange={(e) =>
                  setNewCoupon({ ...newCoupon, stock: e.target.value })
                }
              />

              <div className={styles.datetimeRow}>
                <DatePicker
                  locale={locale}
                  format="YYYY-MM-DD"
                  onChange={(_date, dateString) =>
                    setNewCoupon({
                      ...newCoupon,
                      startDate: dateString as string,
                    })
                  }
                />
                <input
                  type="time"
                  value={newCoupon.startTime}
                  onChange={(e) =>
                    setNewCoupon({ ...newCoupon, startTime: e.target.value })
                  }
                />
              </div>
            </div>

            <div className={styles.modalButtons}>
              <button
                onClick={() => setShowAddModal(false)}
                className={styles.cancelBtn}
              >
                Cancel
              </button>
              <button onClick={confirmAdd} className={styles.confirmBtn}>
                Confirm
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Edit Coupon Modal */}
      {showEditModal && editCoupon && (
        <div className={styles.modalOverlay}>
          <div className={styles.modal}>
            <h3 style={{ marginBottom: "2vh" }}>Edit Coupon</h3>
            <div className={styles.formGroup}>
              <input
                type="text"
                placeholder="Coupon Name"
                value={editCoupon.name}
                onChange={(e) =>
                  setEditCoupon({ ...editCoupon, name: e.target.value })
                }
              />
              <input
                type="number"
                placeholder="Stock"
                value={editCoupon.stock}
                onChange={(e) =>
                  setEditCoupon({ ...editCoupon, stock: e.target.value })
                }
              />
              <div className={styles.datetimeRow}>
                <input
                  type="date"
                  value={editCoupon.startDate}
                  onChange={(e) =>
                    setEditCoupon({ ...editCoupon, startDate: e.target.value })
                  }
                />
                <input
                  type="time"
                  value={editCoupon.startTime}
                  onChange={(e) =>
                    setEditCoupon({ ...editCoupon, startTime: e.target.value })
                  }
                />
              </div>
            </div>

            <div className={styles.modalButtons}>
              <button
                onClick={() => {
                  setShowEditModal(false);
                  setEditCoupon(null);
                }}
                className={styles.cancelBtn}
              >
                Cancel
              </button>
              <button onClick={confirmUpdate} className={styles.confirmBtn}>
                Confirm
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Delete Modal */}
      {showDeleteModal && (
        <div className={styles.modalOverlay}>
          <div className={styles.modal}>
            <div className={styles.modalIcon}>
              <Trash2 className={styles.modalTrash} />
            </div>
            <h3>Confirm Deletion</h3>
            <p>This action cannot be undone. Delete this coupon?</p>
            <div className={styles.modalButtons}>
              <button
                onClick={() => setShowDeleteModal(false)}
                className={styles.cancelBtn}
              >
                Cancel
              </button>
              <button onClick={confirmDelete} className={styles.confirmBtn}>
                Delete
              </button>
            </div>
          </div>
        </div>
      )}

      <div style={{ height: "12vh" }}></div>
    </div>
  );
};

export default CouponList;
