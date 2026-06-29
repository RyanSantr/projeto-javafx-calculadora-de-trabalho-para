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
JAR_PATH="$PACKAGE_INPUT/calculadora-cargas-eletricas.jar"
DIST="$ROOT/dist-linux"
APP_FOLDER="$DIST/CalculadoraCargas"
TAR_PATH="$DIST/CalculadoraCargas-linux.tar.gz"

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

mapfile -d '' SOURCES < <(find "$ROOT/src" -name "*.java" -print0)

javac -encoding UTF-8 -cp "$JAVAFX_LIB/*" -d "$CLASSES" "${SOURCES[@]}"
cp "$ROOT/src/resources/style.css" "$CLASSES/style.css"

jar --create --file "$JAR_PATH" --main-class Main -C "$CLASSES" .

jpackage \
    --type app-image \
    --name CalculadoraCargas \
    --app-version 1.0.0 \
    --vendor "Projeto Universitario" \
    --input "$PACKAGE_INPUT" \
    --main-jar "calculadora-cargas-eletricas.jar" \
    --main-class Main \
    --module-path "$JAVAFX_LIB" \
    --add-modules javafx.controls \
    --dest "$DIST"

for native_target in "$APP_FOLDER/lib/runtime/lib" "$APP_FOLDER/runtime/lib"; do
    if [[ -d "$native_target" ]]; then
        cp "$JAVAFX_LIB"/*.so* "$native_target"/
    fi
done

cat > "$APP_FOLDER/Abrir CalculadoraCargas.sh" <<'LAUNCHER'
#!/usr/bin/env bash
APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec "$APP_DIR/bin/CalculadoraCargas"
LAUNCHER

chmod +x "$APP_FOLDER/Abrir CalculadoraCargas.sh"

tar -czf "$TAR_PATH" -C "$DIST" CalculadoraCargas

echo "App Linux criado em: $APP_FOLDER"
echo "Launcher criado em: $APP_FOLDER/Abrir CalculadoraCargas.sh"
echo "Pacote Linux criado em: $TAR_PATH"
