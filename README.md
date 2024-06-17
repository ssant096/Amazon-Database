# Amazon-Database

## Overview
This application is representative of an Amazon storefront management system where users can log in as customers, managers, or admins. Customers can view nearby stores, browse products, place orders, and view recent orders. Managers have additional functionalities to update product information, view recent product updates, see popular items and customers, and place supply requests to warehouses. Admins can manage user and product information. The application includes helper functions for tasks like selecting items based on distance, and optimizes database interactions with indexes to improve performance.

## Helper Functions

### SelectByDistance
This helper function lets users select among a list of options sorted by distance from an origin. The function is passed the column name of the data (e.g., Store ID), a query result with values (id, latitude, longitude), the origin (latitude, longitude), and an optional display count limit. This is used for selections sorted by distance, such as store or warehouse selection.

## Menu Navigation

### Login Page
Users can log in as a customer, manager, or admin. Creating a new user defaults to a customer, though an admin can later change their type.

![image](https://github.com/ssant096/Amazon-Database/assets/102336530/0d46afec-ce1b-4e68-a796-76ac71e0bb2d)

### Main Menu
After logging in, users are prompted with the Main Menu. The following options are available:

![image](https://github.com/ssant096/Amazon-Database/assets/102336530/7d1db472-dfe0-4230-a15c-9bfb38add43a)

1. **View Stores within 30 miles**
   - Displays all stores within 30 miles, sorted by distance.
   - Uses the provided `calculateDistance` function.

![image](https://github.com/ssant096/Amazon-Database/assets/102336530/6bf1b592-fa2c-4820-ba3c-542b2cad676e)


2. **View Product List**
   - Displays the nearest 10 stores using `SelectByDistance`.
   - After selecting a store, all products with that store ID are displayed.

![image](https://github.com/ssant096/Amazon-Database/assets/102336530/9d724478-30ac-4097-9dc4-0472e14c158c)

![image](https://github.com/ssant096/Amazon-Database/assets/102336530/abec403a-d34d-476f-95c0-81dc378261d6)


3. **Place Order**
   - Prompts users to select a store using `SelectByDistance`.
   - Displays available products at the selected store.
   - Updates the orders and products tables based on the selected product and order amount.

![image](https://github.com/ssant096/Amazon-Database/assets/102336530/450da765-8209-44fd-aed3-e259a75a437d)

![image](https://github.com/ssant096/Amazon-Database/assets/102336530/f3f0e05e-a06e-44a6-8fd0-56e39645dfd6)


4. **View 5 Recent Orders**
   - For customers: Displays their 5 most recent orders.
   - For managers: Prompts to view their own orders or store orders.

![image](https://github.com/ssant096/Amazon-Database/assets/102336530/c7180724-7192-4102-80e5-58f379600f77)

![image](https://github.com/ssant096/Amazon-Database/assets/102336530/e975cc45-37f2-43d3-bb1f-aa622f2be31e)

![image](https://github.com/ssant096/Amazon-Database/assets/102336530/f0afcfbb-731d-4379-aac2-ae73cbd36274)


5. **Update Product** (Managers only)
   - Prompts managers to choose a store they manage.
   - Displays products at the selected store and allows updating units and price.

![image](https://github.com/ssant096/Amazon-Database/assets/102336530/378dafd3-76fc-444e-9649-40bf29c2913b)

![image](https://github.com/ssant096/Amazon-Database/assets/102336530/8c172b3d-52c2-40ce-ad44-dd1f80e3ff3c)


6. **View 5 Recent Product Updates** (Managers only)
   - Prompts managers to select a store and displays the 5 most recent updates at that store.

![image](https://github.com/ssant096/Amazon-Database/assets/102336530/6da6b321-aa27-43f3-abad-36fc4375af54)


7. **View 5 Popular Items** (Managers only)
   - Prompts managers to select a store and displays the 5 most popular items by sell count.

![image](https://github.com/ssant096/Amazon-Database/assets/102336530/c9771d6f-3151-4f50-9a48-d0c5e19e932f)


8. **View 5 Popular Customers** (Managers only)
   - Displays the 5 most popular customers by total units purchased.

![image](https://github.com/ssant096/Amazon-Database/assets/102336530/752e8d13-bbe6-483c-9062-24dec432a36f)


9. **Place Product Supply Request to Warehouse** (Managers only)
   - Prompts managers to select a store and product, then a warehouse and order amount.
   - Updates the product count in the store and the productSupplyRequests table.

![image](https://github.com/ssant096/Amazon-Database/assets/102336530/a5448abc-e8e2-4950-8c71-a452b62685d4)


10. **Admin Options** (Admins only)
    - Allows viewing or updating user or product information.

![image](https://github.com/ssant096/Amazon-Database/assets/102336530/26bccccd-4fea-4539-8ecf-3cd90172eaa8)


## Indexes
Several indexes were added to optimize query performance:
1. Composite index on name and password for user authentication.
2. Foreign key ID indexes for frequent joins and comparisons.
3. Index on user type for frequent queries.
4. Composite index on storeId and productName for queries sorting by productName.
5. Composite index on customerId and orderTime for order retrieval by customer.
6. Index on managerId, storeId, and updateOn for update retrieval.
7. Composite index on storeId for order information retrieval.

## Problems and Findings
- **Administrator Functions**: Solved by creating a 10th option in the main menu for administrators.
- **User ID Retrieval**: Solved by creating a global variable to store the user ID from the login function.
- **Query Results Alignment**: Improved the alignment in `executeQueryAndPrintResults` for better readability.

## Contributions
- **Shan**: Implemented functionalities for browsing stores, browsing products, ordering products, admin functions, and partially implemented product information updates.
- **Chris**: Implemented functionalities for browsing order lists, manager functions, popular product and customer views, product supply requests, partially implemented product information updates, improved menu navigation, and wrote indexes.
