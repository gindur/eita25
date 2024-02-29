#!/bin/bash

# Define directories
OUTPUT_DIR="$(pwd)/certificates"
CA_DIR="$OUTPUT_DIR/CA"
CLIENT_DIR="$OUTPUT_DIR/client"
SERVER_DIR="$OUTPUT_DIR/server"  # Server certificates directory
DB_DIR="$(pwd)/src/database"

# Create directories
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
    local store_dir=$1
    local store_name=$2
    keytool -import -file "$CA_DIR/ca-cert.pem" -alias CA -keystore "$store_dir/$store_name" -storepass password -noprompt
}

# Generate a keystore for a person, create a CSR, sign it, and import both CA and signed certs
create_keystore_for_person() {
    local person_name=$1
    local keystore_dir=$2
    local keystore_name="${person_name}keystore"

    # Generate keypair and keystore
    keytool -genkeypair -alias "${person_name}key" -keyalg RSA -keystore "$keystore_dir/$keystore_name" -storepass password -keypass password -dname "CN=$person_name, O=Organization"

    # Generate CSR
    keytool -certreq -alias "${person_name}key" -keystore "$keystore_dir/$keystore_name" -file "${person_name}.csr" -storepass password

    # Sign CSR with CA
    openssl x509 -req -in "${person_name}.csr" -CA "$CA_DIR/ca-cert.pem" -CAkey "$CA_DIR/ca-key.pem" -CAcreateserial -out "${person_name}-cert.pem" -days 365

    # Import CA cert and signed cert into keystore
    keytool -importcert -file "$CA_DIR/ca-cert.pem" -alias CA -keystore "$keystore_dir/$keystore_name" -storepass password -noprompt
    keytool -importcert -file "${person_name}-cert.pem" -alias "${person_name}key" -keystore "$keystore_dir/$keystore_name" -storepass password -noprompt
    
    # Extract serial number
    serial=$(openssl x509 -in "${person_name}-cert.pem" -serial -noout | cut -d'=' -f2)

    # Cleanup
    rm "${person_name}.csr" "${person_name}-cert.pem"

    # Update serialnumber_userid database
    echo "${serial}:${person_name}" >> "$DB_DIR/serialnumber_userid.txt"
}

# Create server keystore and truststore
create_server_certificates() {
    recreate_folder "$SERVER_DIR"
    create_truststore "$SERVER_DIR" "servertruststore"
    create_keystore_for_person "server" "$SERVER_DIR"
}

# Main setup function
setup() {
    recreate_folder "$OUTPUT_DIR"
    recreate_folder "$CA_DIR"
    recreate_folder "$CLIENT_DIR"
    echo "" > "$DB_DIR/serialnumber_userid.txt"  # Clear the serialnumber_userid file

    # Create CA
    create_ca

    # Create a common truststore for all clients
    create_truststore "$CLIENT_DIR" "clienttruststore"

    # Create client keystores
    local clients=("government" "patient1" "patient2" "nurse1" "nurse2" "doctor1" "doctor2")
    for client in "${clients[@]}"; do
        create_keystore_for_person "$client" "$CLIENT_DIR"
    done

    # Create server keystore and truststore
    create_server_certificates

    echo "Setup complete. Keystores and truststores created for clients in $CLIENT_DIR and for server in $SERVER_DIR"
}

# Run the setup
setup
