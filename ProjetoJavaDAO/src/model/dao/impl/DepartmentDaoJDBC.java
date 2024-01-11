package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepartmentDaoJDBC implements DepartmentDao {
    private Connection conn;

    public DepartmentDaoJDBC(Connection conn) {
        this.conn = conn;
    }


    @Override
    public void insert(Department obj) {
        PreparedStatement st = null; // var p/consulta SQL

        try {
            conn.setAutoCommit(false); // desativando auto confirmação de transações
            // QUERY
            st = conn.prepareStatement("INSERT INTO department\n" +
                            "(Name) \n" +
                            "VALUES \n" +
                            "(?)",
                    Statement.RETURN_GENERATED_KEYS);
            // VALUES
            st.setString(1, obj.getName());

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
    public void update(Department obj) {
        PreparedStatement st = null; // var p/consulta SQL

        try {
            conn.setAutoCommit(false); // desativando auto confirmação de transações
            // QUERY
            st = conn.prepareStatement("UPDATE department \n" +
                    "SET Name = ? \n" +
                    "WHERE Id = ?");
            // VALUES
            st.setString(1, obj.getName());
            st.setInt(2, obj.getId());

            int rowsAffected = st.executeUpdate();
            if (rowsAffected <= 0) {
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
    public void deleteById(Integer id) {
        PreparedStatement st = null; // var p/consulta SQL
        ResultSet rs = null; // var p/resultado da consulta

        try {
            conn.setAutoCommit(false); // desativando auto confirmação de transações
            // QUERY
            st = conn.prepareStatement("DELETE FROM department \n" +
                            "WHERE Id = ?",
                    Statement.RETURN_GENERATED_KEYS);
            // VALUES
            st.setInt(1, id);

            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                rs = st.getGeneratedKeys();
                DB.closeResultSet(rs);
            } else {
                throw new DbException("unexpected error, no rows affected!");
            }

            conn.commit(); // confirmando transação
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(st);
        }
    }

    @Override
    public Department findById(Integer id) {
        PreparedStatement st = null; // var p/consulta SQL
        ResultSet rs = null; // var p/resultado da consulta

        try {
            st = conn.prepareStatement("SELECT *,department.Name as DepName \n" +
                    "FROM department \n" +
                    "WHERE department.Id = ?");
            st.setInt(1, id);

            rs = st.executeQuery();
            if (rs.next()) {
                Department dep = instantiateDepartment(rs);
                return dep;
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
    public List<Department> findAll() {
        PreparedStatement st = null; // var p/consulta SQL
        ResultSet rs = null; // var p/resultado da consulta

        try {
            st = conn.prepareStatement("SELECT department.*,department.Name as DepName \n" +
                    "FROM department \n" +
                    "ORDER BY Id");

            rs = st.executeQuery();

            List<Department> list = new ArrayList<>(); // lista p/armazenar retorno da consulta de vendedores
            Map<Integer, Department> map = new HashMap<>(); // map p/nao ter repetição na instanciação do departamento

            while (rs.next()) {
                Department dep = map.get(rs.getInt("Id"));
                if (dep == null) {
                    dep = instantiateDepartment(rs);
                    map.put(rs.getInt("Id"), dep);
                }

                list.add(dep);
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
        dep.setId(rs.getInt("Id"));
        dep.setName(rs.getString("DepName"));
        return dep;
    }
}
