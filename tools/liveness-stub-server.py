#!/usr/bin/env python3
"""Stub backend keputusan liveness untuk latihan VIDA.

BUKAN backend kantor. Nama field sengaja generik dan berbeda dari yang asli.
Backend asli auth-gated dan hanya bisa diakses dari jaringan internal, jadi
lapisan ini disimulasikan.

Kontrak:
    POST /liveness/decision
        {
          "userId": "string",
          "transactionId": "string|null",
          "livenessScore": 0.0,
          "manipulationScore": 0.0,
          "sessionId": "string|null",
          "source": "REHEARSAL"
        }
    ->  { "action": "ALLOW|BLOCK|REVIEW", "reason": "string", "message": "string" }

    GET /health -> { "status": "UP" }

Aturan keputusan (sengaja sederhana, supaya hasilnya bisa ditebak saat latihan):
    manipulationScore > 0.5  -> BLOCK
    livenessScore    >= 0.8  -> ALLOW
    selain itu               -> REVIEW

Jalur error: kalau userId diawali "fail", server balas HTTP 500. Dipakai untuk
menguji bahwa aplikasi menangani error di jalur terpisah dari happy path.

Pemakaian:
    python tools/liveness-stub-server.py --port 8090
Bind ke 0.0.0.0 supaya bisa dihubungi HP/emulator di jaringan yang sama.
"""
import argparse
import json
import socket
from http.server import BaseHTTPRequestHandler, HTTPServer


def decide(payload: dict) -> dict:
    liveness = float(payload.get("livenessScore") or 0.0)
    manipulation = float(payload.get("manipulationScore") or 0.0)

    if manipulation > 0.5:
        return {
            "action": "BLOCK",
            "reason": "MANIPULATION_SUSPECTED",
            "message": f"Indikasi manipulasi terdeteksi (score {manipulation:.2f}).",
        }
    if liveness >= 0.8:
        return {
            "action": "ALLOW",
            "reason": "LIVENESS_PASSED",
            "message": f"Verifikasi wajah lolos (score {liveness:.2f}).",
        }
    return {
        "action": "REVIEW",
        "reason": "LOW_CONFIDENCE",
        "message": f"Skor liveness {liveness:.2f} di bawah ambang, perlu review manual.",
    }


class Handler(BaseHTTPRequestHandler):
    def _send(self, status: int, body: dict):
        raw = json.dumps(body).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(raw)))
        self.end_headers()
        self.wfile.write(raw)

    def do_GET(self):
        if self.path.rstrip("/") == "/health":
            print(f"GET {self.path} -> 200", flush=True)
            self._send(200, {"status": "UP"})
        else:
            print(f"GET {self.path} -> 404", flush=True)
            self._send(404, {"error": "not found"})

    def do_POST(self):
        if self.path.rstrip("/") != "/liveness/decision":
            print(f"POST {self.path} -> 404", flush=True)
            self._send(404, {"error": "not found"})
            return

        length = int(self.headers.get("Content-Length") or 0)
        raw = self.rfile.read(length) if length else b"{}"
        try:
            payload = json.loads(raw.decode("utf-8"))
        except json.JSONDecodeError:
            print(f"POST {self.path} -> 400 (body bukan JSON valid)", flush=True)
            self._send(400, {"error": "invalid json"})
            return

        user_id = str(payload.get("userId") or "")

        # Jalur error yang disengaja, untuk menguji penanganan error di aplikasi.
        if user_id.lower().startswith("fail"):
            print(f"POST {self.path} userId={user_id} -> 500 (dipaksa)", flush=True)
            self._send(500, {"error": "simulated upstream failure"})
            return

        result = decide(payload)
        print(
            f"POST {self.path} userId={user_id} "
            f"liveness={payload.get('livenessScore')} manipulation={payload.get('manipulationScore')} "
            f"-> 200 {result['action']}",
            flush=True,
        )
        self._send(200, result)

    def log_message(self, fmt, *args):
        pass  # log bawaan dimatikan, format sendiri di atas


def local_ips():
    ips = set()
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(("8.8.8.8", 80))
        ips.add(s.getsockname()[0])
        s.close()
    except OSError:
        pass
    return sorted(ips)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--port", type=int, default=8090)
    ap.add_argument("--host", default="0.0.0.0")
    args = ap.parse_args()

    srv = HTTPServer((args.host, args.port), Handler)
    print(f"liveness stub listening on http://{args.host}:{args.port}", flush=True)
    for ip in local_ips():
        print(f"  dari HP/emulator di jaringan sama : http://{ip}:{args.port}/", flush=True)
    print(f"  dari emulator Android (host loopback): http://10.0.2.2:{args.port}/", flush=True)
    srv.serve_forever()


if __name__ == "__main__":
    main()
