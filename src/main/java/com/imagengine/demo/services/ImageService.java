package com.imagengine.demo.services;


import java.sql.*; // Pour la connexion avec Oracle
import	java.io.*; // Pour les entrée sorties
import	oracle.jdbc.*; // Pour les pilotes Oracle
import	oracle.sql.*; // Pour les spécificités SQL d'Oracle
import	oracle.ord.im.OrdImage; // Pour la classe OrdImage
import	oracle.ord.im.OrdImageSignature; // Pour la classe OrdImageSignature
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;


public class ImageService {
    private OrdImageSignature sign;

    public ImageService(){
    }
    public void initImage(BigDecimal id) {
        // Ecriture de la requÃªte SQL
        String sql = "UPDATE imageTest SET image=ORDSYS.ORDImage.init(), signature=ORDSYS.ORDImageSignature.init() WHERE id=?";

        PreparedStatement stmt = null;
        try {
            // Connect.getConnection().setAutoCommit(true);
            stmt = Connect.getConnection().prepareStatement(sql);
            stmt.setBigDecimal(1, id);
            stmt.executeUpdate();
            Connect.getConnection().commit();
            stmt.close();
            System.out.println("Init Done ");
        } catch (SQLException ex) {
            System.out.println("Init Failed ");
        }
    }

    public OrdImage setProperties(BigDecimal id,File file) {

        PreparedStatement stmt = null;
        // Ecriture de la requÃªte SQL
        String sql = "SELECT image, signature FROM imageTest WHERE id=? FOR UPDATE";

        // Execution de la requÃªte et rÃ©cupÃ©ration du rÃ©sultat
        OracleResultSet rset;
        OrdImage imgObj = null;
//        OrdImageSignature sign=null;
        try {
            stmt = Connect.getConnection().prepareStatement(sql);
            stmt.setBigDecimal(1, id);
            rset = (OracleResultSet) stmt.executeQuery();
            if (rset.next()) // RÃ©cupÃ©ration du descripteur d'OrdImage
            {
                System.out.println("RÃ©cupÃ©ration du descripteur d'OrdImage");
                imgObj = (OrdImage) rset.getORAData(1, OrdImage.getORADataFactory());
                sign = (OrdImageSignature) rset.getORAData(2, OrdImageSignature.getORADataFactory());
            }
            stmt.close();
        } catch (SQLException ex) {
            System.out.println("RÃ©cupÃ©ration du descripteur d'OrdImage #FAILED#");
        }

        // CrÃ©ation d'un bloc try{}catch pour l'exception d'entrÃ©e/sortie
        try {
            // Envoi de l'image dans l'attribut localData du type ORDImage
            byte[] fileContent = Files.readAllBytes(file.toPath());
            imgObj.loadDataFromByteArray(fileContent);
            // GÃ©nÃ©ration des mÃ©tas donnÃ©es (propriÃ©tÃ©s de l'image)
            imgObj.setProperties();
            if(imgObj.checkProperties())
            {   sign.generateSignature(imgObj);
                return imgObj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgObj;
    }

    public void updateAndInsertImage(BigDecimal id,File file) {

        initImage(id);
        try {
            // Connect.getConnection().setAutoCommit(true);
            // GÃ©nÃ©ration des mÃ©tas donnÃ©es (propriÃ©tÃ©s de l'image)
            OrdImage imgObj = setProperties(id, file);
            // VÃ©rification de la gÃ©nÃ©ration des propriÃ©tÃ©s
            if (imgObj.checkProperties()) {
                // Ecriture de la requÃªte SQL pour mettre Ã  jour l'attribut
                String sql = "UPDATE imageTest SET image=? signature=? WHERE id=?";
                // CrÃ©ation d'une instance de l'objet OraclePreparedStatement
                OraclePreparedStatement pstmt = (OraclePreparedStatement) Connect.getConnection().prepareStatement(sql);
                // Ajout de l'instance d'OrdImage dans la requÃªte
                pstmt.setORAData(1, imgObj);
                pstmt.setORAData(2, sign);
                pstmt.setBigDecimal(3, id);
                // Execution de la requÃªte
                pstmt.executeQuery();
                // Connect.getConnection().setAutoCommit(true);
                // Fermeture
                pstmt.close();
                Connect.getConnection().commit();
                Connect.getConnection().setAutoCommit(true);
                Connect.getConnection().close();
                System.out.println("Done updateAndInsertImage");
            }
        } catch (Exception ex) {
            System.out.println("updateAndInsertImage FAILED");
        }
    }
}

