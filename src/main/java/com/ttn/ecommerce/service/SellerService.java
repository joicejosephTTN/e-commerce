package com.ttn.ecommerce.service;

import com.ttn.ecommerce.entity.Address;
import com.ttn.ecommerce.entity.Seller;
import com.ttn.ecommerce.entity.User;
import com.ttn.ecommerce.model.AddressDTO;
import com.ttn.ecommerce.model.SellerProfileDTO;
import com.ttn.ecommerce.repository.SellerRepository;
import com.ttn.ecommerce.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

@Service
public class SellerService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    SellerRepository sellerRepository;

    // method that creates a list of property names to ignore if they are null in source object;
    // and that uses this list to pass as parameter in BeanUtils.copyProperties
    public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for(PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        return emptyNames.toArray(new String[0]);
    }


    public SellerProfileDTO fetchProfile(String email){
        User user = userRepository.findUserByEmail(email);
        Seller seller = user.getSeller();

        SellerProfileDTO sellerProfileDTO = new SellerProfileDTO();

        // converting seller to appropriate responseDTO
        sellerProfileDTO.setUserId(user.getId());
        sellerProfileDTO.setFirstName(user.getFirstName());
        sellerProfileDTO.setLastName(user.getLastName());

        //converting Address to AddressDTO
        Address address = seller.getAddress();
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setAddressLine(address.getAddressLine());
        addressDTO.setCity(address.getCity());
        addressDTO.setState(address.getState());
        addressDTO.setLabel(address.getLabel());
        addressDTO.setCountry(address.getCountry());
        addressDTO.setZipCode(address.getZipCode());


        sellerProfileDTO.setAddress(addressDTO);
        sellerProfileDTO.setActive(user.isActive());
        sellerProfileDTO.setGst(seller.getGst());
        sellerProfileDTO.setCompanyName(seller.getCompanyName());
        sellerProfileDTO.setCompanyContact(seller.getCompanyContact());

        return sellerProfileDTO;
    }

    public String updateProfile(String email, SellerProfileDTO sellerProfileDTO){
        User user = userRepository.findUserByEmail(email);
        Seller seller = user.getSeller();

        BeanUtils.copyProperties(sellerProfileDTO, user, getNullPropertyNames(sellerProfileDTO));
        BeanUtils.copyProperties(sellerProfileDTO, seller, getNullPropertyNames(sellerProfileDTO));
        userRepository.save(user);
        sellerRepository.save(seller);
        return "Updated successfully.";

    }
}

