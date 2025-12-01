

🌿 SAGAlyze

Clinician-Only Smart Dermatology Assistant

> AI assists — Clinician decides.
AI interprets pixels — Physicians interpret patients.



SAGAlyze is an AI-powered dermatology companion app built exclusively for licensed clinicians.
It provides real-time on-device skin analysis, tracks long-term patient progress, and generates secure visual summaries — always keeping diagnostic decisions in the clinician’s hands.


---

✨ Core Highlights

🔍 On-Device AI Dermatology Classification

Detects 537 skin conditions with a lightweight <35 MB TFLite model

Real-time inference (<300ms), fully offline

Top-3 predictions with calibrated confidence

Explainability heatmaps for transparent insights


📈 Longitudinal Progress Tracking

Automated image alignment & normalization

Tracks:

Lesion area

Redness / erythema

Pigmentation

Morphological patterns


Visual tools:

Before↔After slider

Delta heatmaps

ROI zoom

Time-lapse animation



🔐 Clinician-Only Secure Access

Restricted authentication

Offline-first storage (Room DB)

No patient-side uploads

Full audit trail for safety & compliance


📤 Controlled Patient Sharing

Curated, read-only summaries

Secure link or PDF export

Patients see only visuals + simple notes

No diagnosis, no AI predictions, no risk scores



---

🧠 Architecture Overview

Capture → AI Analysis (On-Device) → Clinician Review 
→ Save Visit → Longitudinal Tracking → Summary Generation → Secure Sharing


---

📱 Android Application (Kotlin)

Kotlin + Jetpack Compose for modern UI

MVVM Architecture with clean separation

Room DB for offline patient data

Coroutines + Flow for reactive operations

CameraX for guided dermatology-focused capture

TFLite Interpreter embedded for local ML inference



---

🤖 AI & Imaging Pipeline

Model

MobileNetV3-Small / EfficientNet-Lite

TensorFlow → TFLite optimized conversion

Temperature-scaled confidence calibration

Performance stratified across Fitzpatrick I–VI skin tones


Computer Vision

Powered by OpenCV / Skia:

Image registration (baseline vs follow-up)

Skin-region segmentation

Colorimetric analysis (redness, pigmentation)

Delta heatmap overlay generation



---

🩺 Clinician Workflow

1. Login (clinician-only)


2. Select/Add patient


3. Capture lesion image via guided camera


4. AI classification (on-device)


5. Clinician reviews insights


6. Track multi-visit progress


7. Generate summary (visual + textual)


8. Share securely via PDF / link




---

🛠️ Tech Stack

Mobile App:
Kotlin, Jetpack Compose, MVVM, CameraX, Room DB, Coroutines

AI:
TensorFlow, TensorFlow Lite, MobileNetV3 / EfficientNet-Lite

CV/Imaging:
OpenCV, Skia

Security:
Cryptographically signed links, audit logging

Documentation:
Model Cards, Fairness Reports


---

📊 Evaluation Framework

Category	Weight	Focus

AI Classification	30%	Accuracy, calibration, inference speed
Progress Tracking	25%	Metric reliability, visualization quality
Safety Controls	20%	Clinician-only enforcement, disclaimer use
Fairness Audits	15%	Skin tone equity, ECE, model card
Engineering & UX	10%	Architecture, UI polish, performance



---

🚀 Vision

SAGAlyze redefines how dermatology integrates AI:

AI never replaces clinical judgment

Patients never self-diagnose

Progress becomes objective, visual, and transparent


A platform where responsible AI meets real clinical value.


---

📌 Roadmap

[ ] Cloud sync (HIPAA-aligned)

[ ] Dermatologist analytics dashboard

[ ] Skin tone–aware augmentation pipeline

[ ] Multi-device clinician access

[ ] Multilingual UI



