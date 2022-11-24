package com.ttn.ecommerce.service;

import com.ttn.ecommerce.entity.Address;
import com.ttn.ecommerce.entity.Seller;
import com.ttn.ecommerce.entity.User;
import com.ttn.ecommerce.exception.PasswordDoNotMatchException;
import com.ttn.ecommerce.model.AddressDTO;
import com.ttn.ecommerce.model.AddressUpdateDTO;
import com.ttn.ecommerce.model.SellerUpdateDTO;
import com.ttn.ecommerce.model.SellerViewDTO;
import com.ttn.ecommerce.repository.AddressRepository;
import com.ttn.ecommerce.repository.SellerRepository;
import com.ttn.ecommerce.repository.UserRepository;
import com.ttn.ecommerce.utils.FilterProperties;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


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
        addressDTO.setZipCode(address.getZipCode());

        // converting seller to appropriate responseDTO
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
        AddressUpdateDTO addressDTO = sellerUpdateDTO.getAddress();

        //partial update of address - ignoring null properties received in request
        BeanUtils.copyProperties(addressDTO, address, FilterProperties.getNullPropertyNames(addressDTO));

        // saving updates
        address.setSeller(seller);
        addressRepository.save(address);

        // partial updates
        BeanUtils.copyProperties(sellerUpdateDTO, user, FilterProperties.getNullPropertyNames(sellerUpdateDTO));
        BeanUtils.copyProperties(sellerUpdateDTO, seller, FilterProperties.getNullPropertyNames(sellerUpdateDTO));
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

