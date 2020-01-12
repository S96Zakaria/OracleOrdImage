package com.imagengine.demo.service;


import oracle.ord.im.OrdImage; // Pour la classe OrdImage
import oracle.ord.im.OrdImageSignature; // Pour la classe OrdImageSignature

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
public class ImageService {
    private OrdImageSignature sign;

    public void insertNewImage() {
        String sql = "INSERT INTO IMAGE (id) values(image_seq.NEXTVAL)";
        PreparedStatement stmt = null;
        try {
            // Connect.getConnection().setAutoCommit(true);
            stmt = Connect.getConnection().prepareStatement(sql);
            stmt.executeQuery();
            Connect.getConnection().commit();
            stmt.close();
            System.out.println("Insetion done  ");
        } catch (SQLException ex) {
            System.out.println("Insertion Failed");
        }
    }

    public int getLastId() {
        OracleResultSet rset;
        int x = 0;
        String sql = "SELECT MAX(id) from Image";
        PreparedStatement stmt = null;
        try {
            stmt = Connect.getConnection().prepareStatement(sql);

            rset = (OracleResultSet) stmt.executeQuery();
            if (rset.next()) // RÃ©cupÃ©ration du descripteur d'OrdImage
            {
                System.out.println("RÃ©cupÃ©ration Max ID");
                x = rset.getInt(1);
            }
            Connect.getConnection().commit();

            stmt.close();
            System.out.println("id howa " + x);

        } catch (SQLException ex) {
            System.out.println("RÃ©cupÃ©ration Max ID Failleeed");
        }
        return x;
    }

    public ImageService() {
    }

    public void initImage(BigDecimal id) {
        // Ecriture de la requÃªte SQL
        String sql = "UPDATE image SET image=ORDSYS.ORDImage.init(), signature=ORDSYS.ORDImageSignature.init() WHERE id=?";

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

    public OrdImage setProperties(BigDecimal id, File file) {

        PreparedStatement stmt = null;
        // Ecriture de la requÃªte SQL
        String sql = "SELECT image, signature FROM image WHERE id=? FOR UPDATE";

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
            if (imgObj.checkProperties()) {
                sign.generateSignature(imgObj);
                return imgObj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgObj;
    }

    public void updateAndInsertImage(BigDecimal id, File file) {

        initImage(id);
        try {
            // Connect.getConnection().setAutoCommit(true);
            // GÃ©nÃ©ration des mÃ©tas donnÃ©es (propriÃ©tÃ©s de l'image)
            OrdImage imgObj = setProperties(id, file);
            // VÃ©rification de la gÃ©nÃ©ration des propriÃ©tÃ©s
            if (imgObj.checkProperties()) {
                // Ecriture de la requÃªte SQL pour mettre Ã  jour l'attribut
                String sql = "UPDATE image SET image=? , signature=? WHERE id=?";
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
            System.out.println(ex);

            System.out.println("updateAndInsertImage FAILED");
        }
    }

    public int createImage(File file) {
        this.insertNewImage();
        int x = this.getLastId();
        this.updateAndInsertImage(BigDecimal.valueOf(x), file);
        return x;

    }

    public OrdImage getImage(int id) {

        OrdImage imgObj = null;
        try {
            Statement stmt = Connect.getConnection().createStatement();

            // Ecriture de la requete SQL pour récupérer l'attribut de type ORDImage
            String sql = "SELECT image FROM image e WHERE e.id=" + BigDecimal.valueOf(id) + " FOR UPDATE";
            // Execution de la requête et récupération du résultat
            OracleResultSet rset = (OracleResultSet) stmt.executeQuery(sql);

            // déclaration d'une instance de l'objet OrdImage


            // S'il y a un résultat
            if (rset.next()) {
                // Récupération du descripteur
                imgObj = (OrdImage) rset.getORAData(1, OrdImage.getORADataFactory());


            }
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return imgObj;

    }

    public OrdImageSignature getSignature(int id, OrdImage imgObj) {
        OrdImageSignature imgSig = null;

        try {

            Statement stmt = Connect.getConnection().createStatement();

            // Ecriture de la requête SQL
            String sql3 = "SELECT signature FROM image WHERE id=" + BigDecimal.valueOf(id) + " FOR UPDATE";

            // Exécution de la requête et récupération du résultat
            OracleResultSet rset2 = (OracleResultSet) stmt.executeQuery(sql3);

            // Déclaration d'une instance de l'objet OrdImageSignature


            // S'il y a un résultat
            if (rset2.next()) {
                // Récupération du descripteur
                imgSig = (OrdImageSignature) rset2.getORAData(1, OrdImageSignature.getORADataFactory());

            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return imgSig;


    }


    public String getDescription(OrdImage imgObj) {
        String result = "";

        try {
            if (imgObj.checkProperties()) {
                result =
                        "Source : " + imgObj.getSource() +
                                "Type mime : " + imgObj.getMimeType() +
                                "Format de fichier : " + imgObj.getFormat() +
                                "Hauteur : " + imgObj.getHeight() +
                                "Largeur : " + imgObj.getWidth() +
                                "Poid en bytes : " + imgObj.getContentLength() +
                                "Type : " + imgObj.getContentFormat() +
                                "Compression : " + imgObj.getCompressionFormat();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int stockImageLocaly(int id, OrdImage imgObj) throws IOException, SQLException {
        // Récupération de l'image sur le disque local
        String pathh = System.getProperty("user.dir") + "/src/main/resources/static/images/" + id + ".jpg";
        imgObj.getDataInFile(pathh);
        System.out.println(this.getDescription(imgObj));
        return id;

    }

    public float compareImages(MultipartFile fileOne, MultipartFile fileTwo, int color, int texture, int shape) throws SQLException {
        int file1 = createFileFromMyltiPart(fileOne);
        int file2 = createFileFromMyltiPart(fileTwo);
        OrdImage image1 = getImage(file1);
        OrdImage image2 = getImage(file2);
        OrdImageSignature signature1 = getSignature(file1, image1);
        OrdImageSignature signature2 = getSignature(file2, image2);
        String commande = "color=" + color + " texture=" + texture + " shape=" + shape;
        // Comparaison par évaluation du score
        return OrdImageSignature.evaluateScore(signature1, signature2, commande);


    }

    public float similarityRate(OrdImageSignature signature1, OrdImageSignature signature2, int color, int texture, int shape, float seuil) {

        int similaire = Integer.MIN_VALUE;
        try {
            // Ecriture de la commande définissant les coef. des critères de // comparaisons
            String commande = "color=" + color + " texture=" + texture + " shape=" + shape;
            // Comparaison par évaluation du score
            float f = OrdImageSignature.evaluateScore(signature1, signature2, commande);
            //  Définission du seuil

            // Comparaison par la méthode isSimilar()

            similaire = OrdImageSignature.isSimilar(signature1, signature2, commande, seuil);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return similaire;
    }

    public int createFileFromMyltiPart(MultipartFile multipartFile) {
        int x = Integer.MIN_VALUE;
        String fileLocation = System.getProperty("user.dir") + "/src/main/resources/static/images/";

        String filename = multipartFile.getOriginalFilename();
        System.out.println(filename);
        File file = new File(fileLocation + filename);
        boolean bool = false;
        try {
            multipartFile.transferTo(file);
            x = this.createImage(file);
            bool = file.delete();
            System.out.println("tmèaat ??" + bool);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        return x;
    }
    public void deleteImage(int id){
        try {



                String sql = "DELETE FROM image  WHERE id="+BigDecimal.valueOf(id);
                // CrÃ©ation d'une instance de l'objet OraclePreparedStatement
                OraclePreparedStatement pstmt = (OraclePreparedStatement) Connect.getConnection().prepareStatement(sql);

                // Execution de la requÃªte
                pstmt.executeQuery();
                // Connect.getConnection().setAutoCommit(true);
                // Fermeture
                pstmt.close();
                Connect.getConnection().commit();
                Connect.getConnection().setAutoCommit(true);
                Connect.getConnection().close();
                System.out.println("Done Deleting");

        } catch (Exception ex) {
            System.out.println(ex);

            System.out.println(" FAILED Deleting");
        }

    }
}

