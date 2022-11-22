package com.ttn.ecommerce;

import com.ttn.ecommerce.entity.Address;
import com.ttn.ecommerce.entity.Seller;
import com.ttn.ecommerce.entity.User;
import com.ttn.ecommerce.repository.SellerRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Optional;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SellerRepositoryTests {

    @Autowired
    private SellerRepository sellerRepo;

    @Test
    @Order(1)
    public void saveSellerTest(){
        User user = new User();
        user.setFirstName("Felix");
        user.setMiddleName(".A");
        user.setLastName("Santos");
        user.setEmail("felixsantos@gmail.com");
        user.setPassword("supersecret");

        Address address = new Address();
        address.setCity("Merida");
        address.setState("Yucatan");
        address.setZipCode(1247);
        address.setAddressLine("35 Casa de Montejo");
        address.setCountry("Mexico");
        address.setLabel("Office");


        Seller seller = new Seller();
        seller.setGst("GST0001FFF");
        seller.setCompanyContact("9876543210");
        seller.setCompanyName("SellsThings");


        seller.setUser(user);

        address.setSeller(seller);
        seller.setAddress(address);

        sellerRepo.save(seller);

        Assertions.assertThat(seller.getId()).isGreaterThan(0);

    }

    @Test
    @Order(2)
    public void getSellerTest(){
        Seller seller = sellerRepo.findByGst("GST0001FFF");
        Assertions.assertThat(seller.getGst()).isEqualTo("GST0001FFF");
    }

    @Test
    @Order(3)
    public void updateSellerTest(){
        Seller seller = sellerRepo.findByGst("GST0001FFF");
        seller.setCompanyContact("0123456789");

        Seller sellerUpdated = sellerRepo.save(seller);

        Assertions.assertThat(sellerUpdated.getCompanyContact()).isEqualTo("0123456789");

    }

    @Test
    @Order(4)
    public void deleteSellerTest(){
        Seller seller = sellerRepo.findByGst("GST0001FFF");
        sellerRepo.delete(seller);

        Optional<Seller> optionalSeller = Optional.ofNullable(sellerRepo.findByGst("GST0001FFF"));

        Seller dummySeller = null;

        if(optionalSeller.isPresent()){
            dummySeller = optionalSeller.get();
        }

        Assertions.assertThat(dummySeller).isNull();
    }
}
