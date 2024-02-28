#!/bin/bash

# Define directories
OUTPUT_DIR=$(pwd)/certificates
CA_DIR=$OUTPUT_DIR/CA
PEOPLE_DIR=$OUTPUT_DIR/client
DB_DIR=$(pwd)/src/database

# Create or clear out directories
recreate_folder() {
  if [ -d "$1" ]; then rm -Rf "$1"; fi
  mkdir -p "$1"
}

# Create Certificate Authority (CA)
create_ca() {
    openssl req -x509 -newkey rsa:2048 -days 365 -keyout "$CA_DIR/ca-key.pem" -out "$CA_DIR/ca-cert.pem" -subj "/CN=CA" -nodes
}

# Create a truststore and import the CA certificate
create_truststore() {
    keytool -import -file "$CA_DIR/ca-cert.pem" -alias CA -keystore "$PEOPLE_DIR/truststore" -storepass password -noprompt
}

# Generate a keystore for a person, create a CSR, sign it, and import both CA and signed certs
create_keystore_for_person() {
    local person_name=$1
    local person_id=$2
    local keystore="$PEOPLE_DIR/${person_name}keystore"

    # Generate keypair and keystore
    keytool -genkeypair -alias "${person_name}key" -keyalg RSA -keystore "$keystore" -storepass password -keypass password -dname "CN=$person_name, O=Organization"

    # Generate CSR
    keytool -certreq -alias "${person_name}key" -keystore "$keystore" -file "${person_name}.csr" -storepass password

    # Sign CSR with CA
    openssl x509 -req -in "${person_name}.csr" -CA "$CA_DIR/ca-cert.pem" -CAkey "$CA_DIR/ca-key.pem" -CAcreateserial -out "${person_name}-cert.pem" -days 365

    # Import CA cert and signed cert into keystore
    keytool -importcert -file "$CA_DIR/ca-cert.pem" -alias CA -keystore "$keystore" -storepass password -noprompt
    keytool -importcert -file "${person_name}-cert.pem" -alias "${person_name}key" -keystore "$keystore" -storepass password -noprompt
    
    # Extract serial number
    serial=$(openssl x509 -in "${person_name}-cert.pem" -serial -noout | cut -d'=' -f2)

    # Cleanup
    rm "${person_name}.csr" "${person_name}-cert.pem"

    # Update serialnumber_userid database
    echo "${serial}:${person_name}" >> "$DATABASE_DIR/serialnumber_userid.txt"
}

# Main setup function
setup() {
    recreate_folder "$OUTPUT_DIR"
    recreate_folder "$CA_DIR"
    recreate_folder "$PEOPLE_DIR"
    echo "" > "$DB_DIR/serialnumber_userid.txt" # Clear the serialnumber_userid file

    # Create CA
    create_ca

    # Create a common truststore for all people
    create_truststore

    # Create keystores for specific roles with hard coded IDs
    create_keystore_for_person "government" "1"
    create_keystore_for_person "patient1" "2"
    create_keystore_for_person "patient2" "3"
    create_keystore_for_person "nurse1" "4"
    create_keystore_for_person "nurse2" "5"
    create_keystore_for_person "doctor1" "6"
    create_keystore_for_person "doctor2" "7"

    echo "Setup complete. Keystores and truststore created in $PEOPLE_DIR"
}

# Run the setup
setup
