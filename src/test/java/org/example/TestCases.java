package org.example;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import io.restassured.path.json.JsonPath;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

public class TestCases extends BaseTest{

    String blockHash;
    int totalTransactions;
    String transactionID;
    @Test
    public void getTotalTransactions() {
        blockHash = given().when().get(baseURI+"/block-height/680000").then().extract().asString();
        totalTransactions= given().when().get(baseURI+"/block/"+blockHash).then().extract().path("tx_count");
        assert totalTransactions == 2875 : "Total transactions in block 680000 are not as expected";
    }

    @Test(dependsOnMethods = "getTotalTransactions" )
    public void validateSumOfInputsAndOutputsOfTransactions() {
        int[] arr={100,200,300};
        int totalVin=0;
        int totalVout=0;
        for(int i=0;i<arr.length;i++){
            transactionID= given().when().get(baseURI+"//block/"+blockHash+"/txid/"+arr[i]).then().extract().asString();

            Response response = given().when().get(baseURI+"/tx/"+transactionID);
            JsonPath jsonPath = response.jsonPath();
            ArrayList vinCount = jsonPath.get("vin");
            ArrayList voutCount = jsonPath.get("vout");
            totalVin =totalVin+ vinCount.size();
            totalVout =totalVout+ voutCount.size();
        }
        assert totalVin == 5: "Total vin count is not as expected";
        assert totalVout == 4: "Total vout count is not as expected";
    }
}
