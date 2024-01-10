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
    }
}