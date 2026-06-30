#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAVAFX_VERSION="21.0.4"
JAVAFX_DIR="$ROOT/lib-linux/javafx-sdk-$JAVAFX_VERSION"
JAVAFX_LIB="$JAVAFX_DIR/lib"
JAVAFX_ZIP="$ROOT/lib-linux/openjfx-${JAVAFX_VERSION}_linux-x64_bin-sdk.zip"
JAVAFX_URL="https://download2.gluonhq.com/openjfx/${JAVAFX_VERSION}/openjfx-${JAVAFX_VERSION}_linux-x64_bin-sdk.zip"
CLASSES="$ROOT/out-linux/classes"
PACKAGE_INPUT="$ROOT/out-linux/package"
JAR_PATH="$PACKAGE_INPUT/calculadora-trabalho-eletrico.jar"
DIST="$ROOT/dist-linux"
APP_FOLDER="$DIST/CalculadoraTrabalhoEletrico"
TAR_PATH="$DIST/CalculadoraTrabalhoEletrico-linux.tar.gz"

require_command() {
    if ! command -v "$1" >/dev/null 2>&1; then
        echo "Erro: comando '$1' nao encontrado." >&2
        exit 1
    fi
}

require_command curl
require_command javac
require_command jar
require_command jpackage
require_command tar
require_command unzip

mkdir -p "$ROOT/lib-linux"

if [[ ! -d "$JAVAFX_LIB" ]]; then
    echo "Baixando JavaFX SDK $JAVAFX_VERSION para Linux..."
    curl -L "$JAVAFX_URL" -o "$JAVAFX_ZIP"
    echo "Extraindo JavaFX SDK..."
    unzip -q -o "$JAVAFX_ZIP" -d "$ROOT/lib-linux"
fi

rm -rf "$ROOT/out-linux" "$DIST"
mkdir -p "$CLASSES" "$PACKAGE_INPUT" "$DIST"

mapfile -d '' SOURCES < <(find "$ROOT/src/main/java" -name "*.java" -print0)

javac -encoding UTF-8 -cp "$JAVAFX_LIB/*" -d "$CLASSES" "${SOURCES[@]}"
cp -R "$ROOT/src/main/resources/." "$CLASSES/"

jar --create --file "$JAR_PATH" --main-class br.com.ryan.trabalhoeletrico.Main -C "$CLASSES" .

jpackage \
    --type app-image \
    --name CalculadoraTrabalhoEletrico \
    --app-version 1.0.0 \
    --vendor "Projeto Universitario" \
    --input "$PACKAGE_INPUT" \
    --main-jar "calculadora-trabalho-eletrico.jar" \
    --main-class br.com.ryan.trabalhoeletrico.Main \
    --module-path "$JAVAFX_LIB" \
    --add-modules javafx.controls,javafx.media \
    --java-options "-Dprism.order=sw" \
    --dest "$DIST"

for native_target in "$APP_FOLDER/lib/runtime/lib" "$APP_FOLDER/runtime/lib"; do
    if [[ -d "$native_target" ]]; then
        cp "$JAVAFX_LIB"/*.so* "$native_target"/
    fi
done

cat > "$APP_FOLDER/Abrir CalculadoraTrabalhoEletrico.sh" <<'LAUNCHER'
#!/usr/bin/env bash
APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec "$APP_DIR/bin/CalculadoraTrabalhoEletrico"
LAUNCHER

chmod +x "$APP_FOLDER/Abrir CalculadoraTrabalhoEletrico.sh"

tar -czf "$TAR_PATH" -C "$DIST" CalculadoraTrabalhoEletrico

echo "App Linux criado em: $APP_FOLDER"
echo "Launcher criado em: $APP_FOLDER/Abrir CalculadoraTrabalhoEletrico.sh"
echo "Pacote Linux criado em: $TAR_PATH"
