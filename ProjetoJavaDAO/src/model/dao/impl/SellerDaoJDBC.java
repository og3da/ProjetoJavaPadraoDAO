package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerDaoJDBC implements SellerDao {
    /*
    CLASSE P/ IMPLEMENTAÇÃO DA INTERFACE - SellerDao
     */

    private Connection conn;

    public SellerDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Seller obj) {
        PreparedStatement st = null; // var p/consulta SQL

        try {
            conn.setAutoCommit(false); // desativando auto confirmação de transações
            // QUERY
            st = conn.prepareStatement("INSERT INTO seller\n" +
                    "(Name, Email, BirthDate, BaseSalary, DepartmentId) \n" +
                    "VALUES \n" +
                    "(?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            // VALUES
            st.setString(1, obj.getName());
            st.setString(2, obj.getEmail());
            st.setDate(3, obj.getBirthDate());
            st.setDouble(4, obj.getBaseSalary());
            st.setInt(5, obj.getDepartment().getId());

            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    obj.setId(id);
                }
                DB.closeResultSet(rs);
            } else {
                throw new DbException("unexpected error, no rows affected!");
            }

            conn.commit(); // confirmando transação
        } catch (SQLException e) {
            try {
                conn.rollback();
                throw new DbException("transaction rolled back, cause: " + e.getMessage());
            } catch (SQLException e1) {
                throw new DbException("Error trying to rollback, cause: " + e1.getMessage());
            }
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void update(Seller obj) {

    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public Seller findById(Integer id) {
        PreparedStatement st = null; // var p/consulta SQL
        ResultSet rs = null; // var p/resultado da consulta

        try {
            st = conn.prepareStatement("SELECT seller.*,department.Name as DepName \n" +
                    "FROM seller INNER JOIN department \n" +
                    "ON seller.DepartmentId = department.Id \n" +
                    "WHERE seller.Id = ?");
            st.setInt(1, id);

            rs = st.executeQuery();
            if (rs.next()) {
                Department dep = instantiateDepartment(rs);
                Seller seller = instantiateSeller(rs, dep);
                return seller;
            }
            return null;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(st);
        }
    }

    @Override
    public List<Seller> findByDepartment(Department department) {
        PreparedStatement st = null; // var p/consulta SQL
        ResultSet rs = null; // var p/resultado da consulta

        try {
            st = conn.prepareStatement("SELECT seller.*,department.Name as DepName \n" +
                    "FROM seller INNER JOIN department \n" +
                    "ON seller.DepartmentId = department.Id\n" +
                    "WHERE DepartmentId = ?\n" +
                    "ORDER BY Name");
            st.setInt(1, department.getId());

            rs = st.executeQuery();

            List<Seller> list = new ArrayList<>(); // lista p/armazenar retorno da consulta de vendedores
            Map<Integer, Department> map = new HashMap<>(); // map p/nao ter repetição na instanciação do departamento

            while (rs.next()) {
                Department dep = map.get(rs.getInt("DepartmentId"));
                if (dep == null) {
                    dep = instantiateDepartment(rs);
                    map.put(rs.getInt("DepartmentId"), dep);
                }

                Seller seller = instantiateSeller(rs, dep);
                list.add(seller);
            }
            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(st);
        }
    }

    @Override
    public List<Seller> findAll() {
        PreparedStatement st = null; // var p/consulta SQL
        ResultSet rs = null; // var p/resultado da consulta

        try {
            st = conn.prepareStatement("SELECT seller.*,department.Name as DepName \n" +
                    "FROM seller INNER JOIN department \n" +
                    "ON seller.DepartmentId = department.Id\n" +
                    "ORDER BY Id");

            rs = st.executeQuery();

            List<Seller> list = new ArrayList<>(); // lista p/armazenar retorno da consulta de vendedores
            Map<Integer, Department> map = new HashMap<>(); // map p/nao ter repetição na instanciação do departamento

            while (rs.next()) {
                Department dep = map.get(rs.getInt("DepartmentId"));
                if (dep == null) {
                    dep = instantiateDepartment(rs);
                    map.put(rs.getInt("DepartmentId"), dep);
                }

                Seller seller = instantiateSeller(rs, dep);
                list.add(seller);
            }
            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(st);
        }
    }

    private Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department dep = new Department();
        dep.setId(rs.getInt("DepartmentId"));
        dep.setName(rs.getString("DepName"));
        return dep;
    }

    private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
        Seller obj = new Seller();
        obj.setId(rs.getInt("Id"));
        obj.setName(rs.getString("Name"));
        obj.setEmail(rs.getString("Email"));
        obj.setBaseSalary(rs.getDouble("BaseSalary"));
        obj.setBirthDate(rs.getDate("BirthDate"));
        obj.setDepartment(dep);
        return obj;
    }
}
