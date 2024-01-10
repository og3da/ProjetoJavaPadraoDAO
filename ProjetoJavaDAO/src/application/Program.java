package application;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.Date;
import java.util.List;

public class Program {
    public static void main(String[] args) {

        SellerDao sellerDao = DaoFactory.createSellerDao();

        System.out.println("\n=== TEST 1: seller findById ===");
        Seller seller = sellerDao.findById(3);
        System.out.println(seller);
        System.out.println("===  ===");

        System.out.println("\n=== TEST 2: seller findByDepartment ===");
        Department department = new Department(2, null);
        List<Seller> sellerList = sellerDao.findByDepartment(department);
        sellerList.forEach(System.out::println);
        System.out.println("===  ===");

        System.out.println("\n=== TEST 3: seller findAll ===");
        List<Seller> sellerList2 = sellerDao.findAll();
        sellerList2.forEach(System.out::println);
        System.out.println("===  ===");

        System.out.println("\n=== TEST 4: seller insert ===");
        Seller seller2 = new Seller(null, "gred", "gred@mail.com", new Date(01/01/2024), 4000.00, department);
        sellerDao.insert(seller2);
        System.out.println("Inserted! New id = " + seller2.getId());
        System.out.println("===  ===");

        System.out.println("\n=== TEST 5: seller update ===");
        Seller sellerUpdate = new Seller(9, "gregory", "gregory@mail.com", new Date(01/01/2024), 4500.00, department);
        sellerDao.update(sellerUpdate);
        System.out.println("Updated! New info = " + seller2.toString());
        System.out.println("===  ===");

        System.out.println("\n=== TEST 6: seller deleteById ===");
        sellerDao.deleteById(10);
        System.out.println("Seller Deleted!");
        System.out.println("===  ===");

        System.out.println("\n=== TEST 7: seller findAll to validate seller deleted ===");
        List<Seller> sellerList3 = sellerDao.findAll();
        sellerList3.forEach(System.out::println);
        System.out.println("===  ===");
    }
}