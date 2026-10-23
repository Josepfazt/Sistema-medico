package com.clinica.sistemamedico.service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.stereotype.Service;

@Service
public class TwoFactorAuthService {

    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    // Genera una clave secreta única para el usuario
    public String generarClaveSecreta() {
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    // Genera la URL del código QR que escaneará la App Google Authenticator
    public String obtenerQrCodeUrl(String secretKey, String email) {
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL("ClinicaMedica", email, 
                new GoogleAuthenticatorKey.Builder(secretKey).build());
    }

    // Valida que el código de 6 dígitos ingresado por el usuario sea correcto en tiempo real
    public boolean verificarCodigo(String secretKey, int codigo) {
        return gAuth.authorize(secretKey, codigo);
    }
}