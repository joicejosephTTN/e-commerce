package com.ttn.ecommerce.service;

import com.ttn.ecommerce.entity.Address;
import com.ttn.ecommerce.entity.Seller;
import com.ttn.ecommerce.entity.User;
import com.ttn.ecommerce.exception.PasswordDoNotMatchException;
import com.ttn.ecommerce.model.AddressDTO;
import com.ttn.ecommerce.model.SellerUpdateDTO;
import com.ttn.ecommerce.model.SellerViewDTO;
import com.ttn.ecommerce.repository.AddressRepository;
import com.ttn.ecommerce.repository.SellerRepository;
import com.ttn.ecommerce.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

@Service
public class SellerService {
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    EmailService emailService;

    @Autowired
    AddressRepository addressRepository;

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


    public SellerViewDTO fetchProfile(String email){
        User user = userRepository.findUserByEmail(email);
        Seller seller = user.getSeller();

        SellerViewDTO sellerViewDTO = new SellerViewDTO();

        // converting seller to appropriate responseDTO
        sellerViewDTO.setUserId(user.getId());
        sellerViewDTO.setFirstName(user.getFirstName());
        sellerViewDTO.setLastName(user.getLastName());

        // converting Address to AddressDTO
        Address address = seller.getAddress();
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setAddressLine(address.getAddressLine());
        addressDTO.setCity(address.getCity());
        addressDTO.setState(address.getState());
        addressDTO.setLabel(address.getLabel());
        addressDTO.setCountry(address.getCountry());
        addressDTO.setPinCode(address.getZipCode());


        sellerViewDTO.setAddress(addressDTO);
        sellerViewDTO.setActive(user.isActive());
        sellerViewDTO.setGst(seller.getGst());
        sellerViewDTO.setCompanyName(seller.getCompanyName());
        sellerViewDTO.setCompanyContact(seller.getCompanyContact());

        return sellerViewDTO;
    }

    public String updateProfile(String email, SellerUpdateDTO sellerUpdateDTO){
        // get associated user,seller and address
        User user = userRepository.findUserByEmail(email);
        Seller seller = user.getSeller();
        Address address = seller.getAddress();

        // extract addressDTO object from the incoming request
        AddressDTO addressDTO = sellerUpdateDTO.getAddress();

        //partial update of address - ignoring null properties received in request
        BeanUtils.copyProperties(addressDTO, address, getNullPropertyNames(addressDTO));

        // saving updates
        address.setSeller(seller);
        addressRepository.save(address);

        // partial updates
        BeanUtils.copyProperties(sellerUpdateDTO, user, getNullPropertyNames(sellerUpdateDTO));
        BeanUtils.copyProperties(sellerUpdateDTO, seller, getNullPropertyNames(sellerUpdateDTO));
        // saving updates
        userRepository.save(user);
        sellerRepository.save(seller);
        return "Updated successfully.";

    }

    public String updatePassword(String email, String password, String confirmPass){
        User user = userRepository.findUserByEmail(email);
        if(!password.equals(confirmPass)){
            throw new PasswordDoNotMatchException("Password do not match.");
        }
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        emailService.sendSuccessfulChangeMail(user);
        return "Password updated successfully.";
    }
}

