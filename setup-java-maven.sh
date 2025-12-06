#!/bin/bash

# Create directory for Java tools
mkdir -p ~/java-tools
cd ~/java-tools

# Download Maven
echo "Downloading Maven 3.9.11..."
curl -fsSL https://archive.apache.org/dist/maven/maven-3/3.9.11/binaries/apache-maven-3.9.11-bin.tar.gz -o maven.tar.gz
tar -xzf maven.tar.gz

# Download JDK 21
echo "Downloading JDK 21..."
curl -fsSL "https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.9%2B10/OpenJDK21U-jdk_aarch64_mac_hotspot_21.0.9_10.tar.gz" -o jdk21.tar.gz
tar -xzf jdk21.tar.gz

# Setup environment
echo ""
echo "Installation complete! Add these to your ~/.zshrc:"
echo ""
echo "export JAVA_HOME=\$HOME/java-tools/jdk-21.0.9+10/Contents/Home"
echo "export M2_HOME=\$HOME/java-tools/apache-maven-3.9.11"
echo "export PATH=\$JAVA_HOME/bin:\$M2_HOME/bin:\$PATH"
echo ""

# Create aliases
cat > ~/java-maven-setup.sh << 'EOF'
#!/bin/bash
export JAVA_HOME=$HOME/java-tools/jdk-21.0.9+10/Contents/Home
export M2_HOME=$HOME/java-tools/apache-maven-3.9.11
export PATH=$JAVA_HOME/bin:$M2_HOME/bin:$PATH
EOF

chmod +x ~/java-maven-setup.sh
echo "Setup script created at ~/java-maven-setup.sh"
