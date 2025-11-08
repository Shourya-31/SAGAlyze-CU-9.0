from fastapi import FastAPI, File, UploadFile, Request
from fastapi.responses import JSONResponse
from fastapi.staticfiles import StaticFiles
import uvicorn
import os, shutil
from your_model_script import process_image_dynamic

app = FastAPI(title="Skin Disease Detection API", version="1.0")
UPLOAD_DIR = "uploads"
os.makedirs(UPLOAD_DIR, exist_ok=True)
app.mount("/uploads", StaticFiles(directory=UPLOAD_DIR), name="uploads")

@app.get("/")
def home():
    return {"status": "running", "message": "Backend active 🚀"}

@app.post("/predict")
async def predict(request: Request, file: UploadFile = File(...)):
    try:
        input_path = os.path.join(UPLOAD_DIR, file.filename)
        with open(input_path, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)

        base_url = str(request.base_url).rstrip("/")
        output = process_image_dynamic(input_path, base_url=base_url)

        return JSONResponse(content={"status": "success", **output})

    except Exception as e:
        return JSONResponse(content={
            "status": "error",
            "message": f"⚠️ Error: {str(e)}"
        }, status_code=500)

if __name__ == "__main__":
    uvicorn.run("app:app", host="0.0.0.0", port=8000, reload=True)
