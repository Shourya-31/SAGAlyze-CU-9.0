

SAGAlyze — Clinician-Only Smart Dermatology Assistant

SAGAlyze is an AI-powered mobile platform designed exclusively for licensed clinicians to assist in dermatologic evaluation, progress tracking, and secure patient communication.
It is not a patient-facing diagnostic tool — all interpretations remain with the clinician.

SAGAlyze focuses on three pillars:
Clinical Safety, Fairness & Transparency, and Human-Centered AI.




✨ Key Features

1. On-Device AI Dermatology Classifier

Detects 537 dermatologic conditions (acne, eczema, tinea, vitiligo, benign lesions, etc.).

Runs fully offline with <300 ms inference time and <35 MB model size.

Provides:

Top-3 predictions with calibrated confidence

Explainability heatmaps

Risk band classifications


Ensures “clinician judgment prevails” as the core design principle.


2. Longitudinal Progress Tracking

Automated image alignment and normalization across visits

Lesion segmentation and measurement of:

Area

Redness (erythema index)

Pigmentation

Patch morphology


Advanced visual tools:

Before/After slider

Heatmap delta overlays

Time-lapse animations

ROI zoom comparisons



3. Controlled Patient-Facing Summaries

Clinicians curate what is shared with patients

Only visual progress + simple notes — no diagnosis or AI predictions

Uses:

PDF summaries

Secure, cryptographically signed links


Enforces strict clinical safety and privacy boundaries.


4. Clinician-Only Secure Access

Authentication restricted to registered clinicians

Access logging + audit trails

Patient cannot upload images or modify data




🧠 Technical Architecture

AI Module

MobileNetV3-Small / EfficientNet-Lite

TFLite model optimized for:

Speed

Small footprint

Offline use


Confidence calibration (Temperature Scaling / Isotonic Regression)

Fairness audits across Fitzpatrick skin types I–VI

Model card with:

Confusion matrices

ECE

Limitations

Dataset demographics



Android App (Kotlin)

Kotlin + Jetpack Compose UI

MVVM Architecture

Room DB for offline patient record storage

Coroutines + Flow for async data and processing

Built-in CameraX integration for guided image capture

TFLite inference integrated at the edge for privacy


Imaging Pipeline

OpenCV / Skia for:

Registration

Cropping

Segmentation

Colorimetric analysis

Heatmap overlays



Sharing Module

Secure PDF/Link generation

Expiry-based access

Read-only summaries

HIPAA-aligned design principles




📱 Clinician Workflow

1. Login (Clinician-only secure access)


2. Select or add patient


3. Capture high-resolution lesion image


4. Run AI analysis (on-device)


5. Clinician interprets results


6. Save visit and track progress over time


7. Generate curated patient progress summary


8. Share via secure link or PDF





📊 Evaluation & Metrics

The system is evaluated using a weighted rubric:

30% AI Classification Quality

25% Progress-Tracking Accuracy

20% Safety Controls

15% Fairness Audits

10% Engineering & UX




🛠️ Tech Stack

Mobile: Kotlin, Jetpack Compose, MVVM, Room, CameraX
ML/AI: TensorFlow, TensorFlow Lite, MobileNetV3/EfficientNet-Lite
Imaging: OpenCV, Skia
Security: Signed URLs, Audit Logging
Documentation: Model Cards, Fairness Reports



📂 Project Deliverables

Full source code repository

Android app (production-ready build)

Optimized TFLite model

Longitudinal comparison module

Secure patient-sharing system

Model Card + Fairness Audit

Demo video showcasing clinician workflow




🚀 Vision

SAGAlyze aims to transform dermatology by combining clinician expertise with ethical, transparent artificial intelligence — not replacing judgment, but enhancing it.

AI assists — clinician decides.
AI interprets pixels — physician interprets patients.

