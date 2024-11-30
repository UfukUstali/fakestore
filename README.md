# THIS PROJECT IS PROBABLY NOT WORKING DUE TO THE [DummyJSON](https://dummyjson.com) API NOT WORKING.

# FakeStore

FakeStore is a simple console-based application that simulates an online store. Users can explore products, view
categories, and interact with a shopping cart. This README provides essential information to get started, including
program features, required libraries, resources, program arguments, and instructions for testing the model in JShell.

## Features

1. **Show all products**

   - Display all products by category.
   - Search for products based on keywords.

2. **Show all categories**

   - View a list of all available product categories.

3. **Cart Management**
   - Add products to the cart.
   - Remove products from the cart.
   - Update cart contents.
   - View stock availability.
   - Monitor account balance.

## Libraries

The following libraries are used in this project:

- `processing.core`: Core library for visual elements.
- `junit`: Unit testing framework.
- `gson`: Library for JSON processing.

## Resources

- [DummyJSON](https://dummyjson.com): Used to generate dummy data for testing.

## Program Arguments

- `format: "arg=value"`
- `--trackpad`: Set to `true` if using a trackpad instead of a mouse (optional, default: `false`).
- `--balance`: Sets the initial user balance (optional, default: random between `5000` and `10000`).

## Testing in JShell

```bash
cd ${FakeStoreRootDir}
// make sure the project is built before running the following command
jshell --class-path .\out\production\fakestore
```

In JShell:

```java
import fakestore.model.IModel;
import fakestore.model.Product;

IModel model=IModel.getInstance();
        model.setArgs(new String[]{"999"});
        Product product=Product.getDummyProduct();
        model.getBalance();
        model.buy(product);
        model.getBalance();
```

Feel free to explore and enjoy your FakeStore experience! But before that a few examples on what to expect:

## Home Page

![Home Page](/images/readme/home.png)

## Category Page

![Category Page](/images/readme/category.png)

## Product Page

![Product Page](/images/readme/product.png)

## Cart Page

![Cart Page](/images/readme/cart.png)
