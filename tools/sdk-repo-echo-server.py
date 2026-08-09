#!/usr/bin/env python3
"""Echo server yang berpura-pura jadi maven repo SDK, untuk latihan VIDA.

Gunanya dua:
  1. Membuktikan Gradle benar-benar mengirim header `x-api-key` saat menarik
     artifact dari maven repo ber-HttpHeaderCredentials.
  2. Menunjukkan URL mana yang sebenarnya dihubungi Gradle, sehingga jebakan
     heuristik `isProdBuild` di settings.gradle.kts terlihat hitam di atas
     putih.

Server ini melayani satu artifact stub kosong:
    com.vida.rehearsal:vida-sdk-stub:1.0.0
Tidak ada SDK asli yang terlibat, tidak ada artifact asli yang ditarik.

ATURAN: nilai header TIDAK PERNAH dicatat. Hanya ada/tidak dan panjangnya.

Pemakaian:
    python tools/sdk-repo-echo-server.py --port 8081 --label sandbox
"""
import argparse
import io
import zipfile
from http.server import BaseHTTPRequestHandler, HTTPServer

GROUP_PATH = "com/vida/rehearsal/vida-sdk-stub/1.0.0"
ARTIFACT = "vida-sdk-stub-1.0.0"

POM = f"""<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.vida.rehearsal</groupId>
  <artifactId>vida-sdk-stub</artifactId>
  <version>1.0.0</version>
  <packaging>jar</packaging>
  <name>VIDA SDK stub (latihan)</name>
  <description>Artifact kosong. Bukan SDK asli.</description>
</project>
""".encode("utf-8")


def empty_jar() -> bytes:
    """JAR kosong yang tetap valid sebagai zip."""
    buf = io.BytesIO()
    with zipfile.ZipFile(buf, "w", zipfile.ZIP_DEFLATED) as z:
        z.writestr("META-INF/MANIFEST.MF", "Manifest-Version: 1.0\n")
    return buf.getvalue()


JAR = empty_jar()


class Handler(BaseHTTPRequestHandler):
    label = "?"

    def _describe_key(self) -> str:
        """Laporkan keberadaan header saja. Nilainya tidak pernah dicatat."""
        value = self.headers.get("x-api-key")
        if value is None:
            return "x-api-key=ABSENT"
        if value == "":
            return "x-api-key=EMPTY"
        return f"x-api-key=PRESENT(len={len(value)})"

    def _body_for(self):
        if self.path.endswith(f"{GROUP_PATH}/{ARTIFACT}.pom"):
            return POM, "text/xml"
        if self.path.endswith(f"{GROUP_PATH}/{ARTIFACT}.jar"):
            return JAR, "application/java-archive"
        return None, None

    def _handle(self, write_body: bool):
        body, ctype = self._body_for()
        status = 200 if body is not None else 404
        print(
            f"[{self.label}] {self.command} {self.path}  {self._describe_key()}  -> {status}",
            flush=True,
        )
        self.send_response(status)
        if body is None:
            self.send_header("Content-Length", "0")
            self.end_headers()
            return
        self.send_header("Content-Type", ctype)
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        if write_body:
            self.wfile.write(body)

    def do_GET(self):
        self._handle(write_body=True)

    def do_HEAD(self):
        self._handle(write_body=False)

    def log_message(self, fmt, *args):
        pass  # log bawaan dimatikan, kita pakai format sendiri di _handle


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--port", type=int, required=True)
    ap.add_argument("--label", required=True, help="sandbox / prod")
    args = ap.parse_args()

    Handler.label = args.label
    srv = HTTPServer(("127.0.0.1", args.port), Handler)
    print(f"[{args.label}] echo maven repo listening on http://127.0.0.1:{args.port}/maven", flush=True)
    srv.serve_forever()


if __name__ == "__main__":
    main()
