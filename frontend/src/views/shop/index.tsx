import {
  Clock,
  Coffee,
  Gift,
  IceCream,
  MapPin,
  Pizza,
  Search,
  Star,
  Utensils,
} from "lucide-react";
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./index.module.scss";

const Shop: React.FC = () => {
  const navigate = useNavigate();

  const [searchQuery, setSearchQuery] = useState("");
  const categories = [
    { icon: <Utensils />, name: "Food", color: "orange" },
    { icon: <Coffee />, name: "Coffee & Tea", color: "amber" },
    { icon: <Pizza />, name: "Fast Food", color: "red" },
    { icon: <IceCream />, name: "Dessert", color: "pink" },
  ];
  const restaurants = [
    {
      id: 1,
      name: "Mixue",
      image:
        "https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=400&h=300&fit=crop",
      rating: 4.6,
      sales: 8901,
      deliveryTime: "20 mins",
      distance: "600 m",
      tags: ["Milk Tea", "Ice Cream"],
      discount: "Save $5 on $20+",
    },
  ];

  return (
    <div className={styles.page}>
      {/* Header */}
      <div className={styles.header}>
        <div className={styles.headerInner}>
          <div className={styles.locationBox}>
            <div className={styles.locationRow}>
              <MapPin className={styles.mapIcon} />
              <span>Current Location</span>
            </div>
            <h1>Singapore · Jurong West</h1>
          </div>

          {/* Search */}
          <div className={styles.searchBox}>
            <Search className={styles.searchIcon} />
            <input
              type="text"
              placeholder="Search restaurants or food..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className={styles.searchInput}
            />
          </div>
        </div>
      </div>

      {/* Coupons */}
      <div
        className={styles.section}
        onClick={() => {
          navigate("/userCouponGrab");
        }}
      >
        <div className={styles.sectionTitle}>
          <div className={styles.titleLeft}>
            <Gift className={styles.titleIcon} />
            <h2>Coupon Center</h2>
          </div>
          <button className={styles.moreButton}>More &gt;</button>
        </div>
        <div>Go to coupon center to find more</div>
      </div>

      {/* Categories */}
      <div className={styles.section}>
        <h2 className={styles.sectionHeader}>Categories</h2>
        <div className={styles.categoryGrid}>
          {categories.map((category, idx) => (
            <button key={idx} className={styles.categoryButton}>
              <div className={`${styles.iconBox} ${styles[category.color]}`}>
                {category.icon}
              </div>
              <span>{category.name}</span>
            </button>
          ))}
        </div>
      </div>

      {/* Restaurants */}
      <div className={styles.section}>
        <h2 className={styles.sectionHeader}>Recommended Restaurants</h2>
        <div className={styles.restaurantGrid}>
          {restaurants.map((res) => (
            <div key={res.id} className={styles.restaurantCard}>
              <div className={styles.imageBox}>
                <img src={res.image} alt={res.name} />
                <div className={styles.discountBadge}>{res.discount}</div>
              </div>
              <div className={styles.restaurantInfo}>
                <h3>{res.name}</h3>
                <div className={styles.infoRow}>
                  <div className={styles.rating}>
                    <Star className={styles.starIcon} />
                    <span>{res.rating}</span>
                  </div>
                  <span>Monthly Sales {res.sales}</span>
                  <div className={styles.time}>
                    <Clock className={styles.clockIcon} />
                    <span>{res.deliveryTime}</span>
                  </div>
                </div>
                <div className={styles.tagsRow}>
                  <div className={styles.tags}>
                    {res.tags.map((tag, i) => (
                      <span key={i}>{tag}</span>
                    ))}
                  </div>
                  <span className={styles.distance}>{res.distance}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      <div style={{ height: "8vh" }}></div>
    </div>
  );
};

export default Shop;
