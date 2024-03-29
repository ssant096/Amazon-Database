DROP INDEX IF EXISTS idx_users_name_password;
DROP INDEX IF EXISTS idx_users_userid;
DROP INDEX IF EXISTS idx_store_storeid;
DROP INDEX IF EXISTS idx_store_managerid;
DROP INDEX IF EXISTS idx_orders_customerid;
DROP INDEX IF EXISTS idx_users_type_userid;
DROP INDEX IF EXISTS idx_product_storeid_productname;
DROP INDEX IF EXISTS idx_orders_customerid_ordertime;
DROP INDEX IF EXISTS idx_productupdates_managerid_storeid_updatedon;
DROP INDEX IF EXISTS idx_orders_storeid_productname;

--1. Composite index for user authentication
CREATE INDEX idx_users_name_password
ON USERS (name, password);

--2. Indexes on Foreign Key Columns
CREATE INDEX idx_users_userid
ON USERS (userID);

CREATE INDEX idx_store_storeid
ON STORE (storeId);

CREATE INDEX idx_store_managerid
ON STORE (managerID);

CREATE INDEX idx_orders_customerid
ON ORDERS (customerID);

--3. Composite index for user type and ID searches
CREATE INDEX idx_users_type_userid
ON USERS (type, userID);

--4. Composite index for product searches by store and product name ordering and for inventory management
CREATE INDEX idx_product_storeid_productname
ON PRODUCT (storeId, productName);

--5. Composite index for orders retrieval and ordering by time
CREATE INDEX idx_orders_customerid_ordertime
ON ORDERS (customerID, orderTime DESC);

--6. Composite index for recent updates in product updates
CREATE INDEX idx_productupdates_managerid_storeid_updatedon
ON PRODUCTUPDATES (managerID, storeId, updatedOn DESC);

--7. Composite index for aggregate sales on orders
CREATE INDEX idx_orders_storeid_productname
ON ORDERS (storeId, productName);