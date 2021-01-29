package local;

import app.item.Asset;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import java.util.ArrayList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class AssetDAOImplTest {

    @Mock
    private AssetDAOImpl assetDAOImpl;

    @Before
    public void setup() {
        assetDAOImpl = new AssetDAOImpl();
    }

    @Test
    public void testGetAssetsToUpdate() {
        ArrayList<Asset> assets = assetDAOImpl.getAssetsToUpdate();
        assertNotNull(assets);
    }

    @Test
    public void testDeleteAssetByID() throws Exception {
//        assetDAOImpl.deleteAssetByID(999);
//        Mockito.verify(assetDAOImpl, times(1)).deleteAssetByID(999);
    }

    @Test
    public void testGetAttributesNameFromAssetID() {
        ArrayList<String> attributeNames = assetDAOImpl.getAttributesNameFromAssetID(1);
        assertNotNull(attributeNames);
    }

    @Test
    public void testGetAssetsFromAssetTypeID() {
        ArrayList<Asset> assets = assetDAOImpl.getAssetsFromAssetTypeID(1);
        assertNotNull(assets);
    }

    @Test
    public void testAssetTypeNameFromID() {
        String name = assetDAOImpl.getAssetTypeNameFromID("1");
        assertNotNull(name);
    }

    @Test
    public void testGetAllLiveAssets() {
        ArrayList<Asset> assets = assetDAOImpl.getAllLiveAssets();
        assertNotNull(assets);
    }

    @Test
    public void testGetAllLiveAssetsDes() {
        ArrayList<Asset> assets = assetDAOImpl.getAllLiveAssetsDes();
        assertNotNull(assets);
    }

    @Test
    public void testAddRulEstimation() {

    }

    @Test
    public void testInsetAsset() {

    }

    @Test
    public void testUpdateRecommendation() {

    }

    @Test
    public void testResetAssetUpdate() {

    }

    @Test
    public void testSetAssetUpdate() {

    }

    @Test
    public void testCreateAssetFromQueryResult() {

    }

    @Test
    public void testCreateAssetInfo() {

    }
}