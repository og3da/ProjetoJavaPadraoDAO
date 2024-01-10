package model.dao;

import model.dao.impl.SellerDaoJDBC;

public class DaoFactory {
    /*
    CLASSE P/ INSTANCIAR CLASSES DAO
     */

    public static SellerDao createSellerDao() {
        return new SellerDaoJDBC();
    }
}
