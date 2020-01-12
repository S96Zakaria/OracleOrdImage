package com.imagengine.demo.service;


import com.imagengine.demo.bean.Compare;
import com.imagengine.demo.bean.Image;
import oracle.ord.im.OrdImage; // Pour la classe OrdImage
import oracle.ord.im.OrdImageSignature; // Pour la classe OrdImageSignature

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
public class ImageService {
    private OrdImageSignature sign;

    public void insertNewImage() {
        String sql = "INSERT INTO IMAGES (id) values(image_seq.NEXTVAL)";
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
        String sql = "SELECT MAX(id) from Images";
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
        String sql = "UPDATE images SET image=ORDSYS.ORDImage.init(), signature=ORDSYS.ORDImageSignature.init() WHERE id=?";

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
        String sql = "SELECT image, signature FROM images WHERE id=? FOR UPDATE";

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
                String sql = "UPDATE images SET image=? , signature=? WHERE id=?";
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
            String sql = "SELECT image FROM images e WHERE e.id=" + BigDecimal.valueOf(id) + " FOR UPDATE";
            OracleResultSet rset = (OracleResultSet) stmt.executeQuery(sql);
            if (rset.next()) {
                imgObj = (OrdImage) rset.getORAData(1, OrdImage.getORADataFactory());
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return imgObj;
    }

    public OrdImageSignature getSignature(int id) {
        OrdImageSignature imgSig = null;
        try {
            Statement stmt = Connect.getConnection().createStatement();
            String sql3 = "SELECT signature FROM images WHERE id=" + BigDecimal.valueOf(id) + " FOR UPDATE";
            OracleResultSet rset2 = (OracleResultSet) stmt.executeQuery(sql3);
            if (rset2.next()) {
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

    public void stockImageLocaly(Image image) throws IOException, SQLException {
        String pathh = System.getProperty("user.dir") + "/uploadingDir/" + image.getId() + ".jpg";
        image.getImage().getDataInFile(pathh);
        System.out.println(this.getDescription(image.getImage()));
    }

    public Compare compareImages(MultipartFile fileOne, MultipartFile fileTwo, float color, float texture, float shape) throws SQLException, IOException {
        Image image1=new Image();
        Image image2=new Image();

    	int id_1 = createFileFromMultiPart(fileOne);
        int id_2 = createFileFromMultiPart(fileTwo);

        OrdImage o_imag1 = getImage(id_1);
        OrdImage o_imag2 = getImage(id_2);
        OrdImageSignature signature1 = getSignature(id_1);
        OrdImageSignature signature2 = getSignature(id_2);
        image1.setId(BigDecimal.valueOf(id_1));
        image2.setId(BigDecimal.valueOf(id_2));
        image1.setImage(o_imag1);
        image2.setImage(o_imag2); 
        System.out.println(image1);
        System.out.println(image2);

        this.stockImageLocaly(image1);
        this.stockImageLocaly(image2);
       
       Compare compare=new Compare();
       compare.setId1(image1.getId());
       compare.setId2(image2.getId());

        String commande = "color=" + color + " texture=" + texture + " shape=" + shape;
        // Comparaison par évaluation du score
        float score = 100 - OrdImageSignature.evaluateScore(signature1, signature2, commande);
        compare.setScore(score);
        System.out.println(commande);
        System.out.println(score);
        this.deleteImage(id_1);
        this.deleteImage(id_2);
        return compare;
    }

    public List<Image> similarityRate(int id, float color, float texture, float shape, float seuil) throws IOException {
        String commande = "color=" + color + " texture=" + texture + " shape=" + shape;
        OrdImageSignature signature1 = getSignature(id);
        List<Image> images = this.getAllImages(id);
        List<Image> resultList = new ArrayList<>();
        for (Image image : images) {
            try {
                System.out.print("## IS SIMILAR "+ OrdImageSignature.isSimilar(signature1, image.getSignature(), commande, seuil));
                if (OrdImageSignature.isSimilar(signature1, image.getSignature(), commande, seuil) == 1) {
                	this.stockImageLocaly(image);
                    image.setScore( 100 - OrdImageSignature.evaluateScore(signature1, image.getSignature(), commande));
                    resultList.add(image);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("\n");
        return resultList;
    }

    public int createFileFromMultiPart(MultipartFile multipartFile) {
        int x = Integer.MIN_VALUE;
        String fileLocation = System.getProperty("user.dir") + "/uploadingDir/";
        String filename = multipartFile.getOriginalFilename();
        System.out.println("Chosen File Name Is: "+filename);
        File file = new File(fileLocation + filename);
        boolean bool = false;
        try {
            multipartFile.transferTo(file);
            x = this.createImage(file);
            bool = file.delete();
            System.out.println("Image Deleted From Local?" + bool);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return x;
    }

    public void deleteImage(int id) {
        try {
            String sql = "DELETE FROM images  WHERE id=" + BigDecimal.valueOf(id);
            OraclePreparedStatement pstmt = (OraclePreparedStatement) Connect.getConnection().prepareStatement(sql);

            pstmt.executeQuery();

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

    public List<Image> getAllImages(int id) {
        List<Image> images = new ArrayList<>();
        String sql = "SELECT id,image,signature FROM images WHERE id<>"+id;

        try {
    		OraclePreparedStatement stmt = (OraclePreparedStatement) Connect.getConnection().prepareStatement(sql);
            OracleResultSet rset = (OracleResultSet) stmt.executeQuery();
            while (rset.next()) {
                Image image = new Image();
                image.setId(BigDecimal.valueOf(rset.getInt(1)));
                image.setImage((OrdImage) rset.getORAData(2, OrdImage.getORADataFactory()));
                image.setSignature((OrdImageSignature) rset.getORAData(3, OrdImageSignature.getORADataFactory()));
                images.add(image);
               System.out.println(image.toString()); 
            }
            stmt.close();
            return images;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
  
    
}
