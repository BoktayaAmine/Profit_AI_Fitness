from flask import Flask, request, jsonify
from flask_cors import CORS
from dotenv import load_dotenv
import google.generativeai as genai
import os
import re
import json

app = Flask(__name__)
CORS(app)

load_dotenv()
api_key = os.getenv("API")
genai.configure(api_key=api_key)

exercises = [
    {"id": 1, "image": "pushups", "niveau": "intermediate", "nom": "pushups", "type": "COUNT"},
    {"id": 2, "image": "cycle", "niveau": "beginner", "nom": "cycle", "type": "DISTANCE"},
    {"id": 3, "image": "hiking", "niveau": "intermediate", "nom": "hiking", "type": "DISTANCE"},
    {"id": 4, "image": "plank", "niveau": "advanced", "nom": "plank", "type": "DURATION"},
    {"id": 5, "image": "running", "niveau": "intermediate", "nom": "running", "type": "DISTANCE"},
    {"id": 6, "image": "squats", "niveau": "beginner", "nom": "squats", "type": "COUNT"},
    {"id": 7, "image": "indoorcycling", "niveau": "beginner", "nom": "indoor cycling", "type": "DURATION"},
    {"id": 8, "image": "stepup", "niveau": "intermediate", "nom": "stepup", "type": "COUNT"},
    {"id": 9, "image": "stretching", "niveau": "beginner", "nom": "stretching", "type": "DURATION"},
    {"id": 10, "image": "swimming", "niveau": "advanced", "nom": "swimming", "type": "DISTANCE"},
    {"id": 11, "image": "climbing", "niveau": "advanced", "nom": "climbing", "type": "DISTANCE"},
    {"id": 12, "image": "deadLifts", "niveau": "advanced", "nom": "deadLifts", "type": "COUNT"},
    {"id": 13, "image": "elliptical", "niveau": "advanced", "nom": "elliptical", "type": "DURATION"},
    {"id": 14, "image": "kettlebell", "niveau": "advanced", "nom": "kettlebell", "type": "COUNT"},
    {"id": 15, "image": "SquatJumps", "niveau": "advanced", "nom": "Squat_Jumps", "type": "COUNT"},
    {"id": 16, "image": "Indoorcycling", "niveau": "advanced", "nom": "Indoor cycling", "type": "DURATION"},
    {"id": 17, "image": "chairpushup", "niveau": "advanced", "nom": "chair pushup", "type": "COUNT"},
    {"id": 18, "image": "dumbbell", "niveau": "advanced", "nom": "dumbbell", "type": "COUNT"},
    {"id": 19, "image": "pistolsquat", "niveau": "advanced", "nom": "pistol_squat", "type": "COUNT"},
    {"id": 20, "image": "sideplank", "niveau": "advanced", "nom": "side-plank", "type": "DURATION"},
    {"id": 21, "image": "situps", "niveau": "beginner", "nom": "sit-ups", "type": "COUNT"},
    {"id": 22, "image": "dips", "niveau": "advanced", "nom": "dips", "type": "COUNT"},
    {"id": 23, "image": "jumpjack", "niveau": "beginner", "nom": "jump-jack", "type": "COUNT"},
    {"id": 24, "image": "bagworkout", "niveau": "advanced", "nom": "bag workout", "type": "DURATION"},
    {"id": 25, "image": "jumprope", "niveau": "intermediate", "nom": "jump-rope", "type": "DURATION"},
    {"id": 26, "image": "kneeup", "niveau": "beginner", "nom": "knee up", "type": "COUNT"},
    {"id": 27, "image": "pullups", "niveau": "advanced", "nom": "pullups", "type": "COUNT"},
    {"id": 28, "image": "chairdips", "niveau": "intermediate", "nom": "chair dips", "type": "COUNT"},
    {"id": 29, "image": "tireflip", "niveau": "advanced", "nom": "tire flip", "type": "COUNT"},
    {"id": 30, "image": "sledgehammer", "niveau": "advanced", "nom": "hammer tire", "type": "COUNT"},
    {"id": 31, "image": "jumpingjacks", "niveau": "beginner", "nom": "jumping jacks", "type": "COUNT"},
    {"id": 32, "image": "tricepextensions", "niveau": "advanced", "nom": "tricep extensions", "type": "COUNT"},
    {"id": 33, "image": "battleropes", "niveau": "intermediate", "nom": "battle ropes", "type": "DURATION"},
    {"id": 34, "image": "burpees", "niveau": "intermediate", "nom": "burpees", "type": "COUNT"},
    {"id": 35, "image": "mountainclimbers", "niveau": "beginner", "nom": "mountain climbers", "type": "COUNT"},
    {"id": 36, "image": "legraises", "niveau": "beginner", "nom": "leg raises", "type": "COUNT"},
    {"id": 37, "image": "sprintintervals", "niveau": "advanced", "nom": "sprint intervals", "type": "DURATION"},
    {"id": 38, "image": "treadmillrunning", "niveau": "beginner", "nom": "treadmill running", "type": "DISTANCE"},
    {"id": 39, "image": "corestability", "niveau": "advanced", "nom": "core stability exercises", "type": "DURATION"},
    {"id": 40, "image": "barbellsquat", "niveau": "advanced", "nom": "barbell squat", "type": "COUNT"},
]



## *********************************************************************** ##
def create_personalized_prompt(user_data, objectives):
    completed_objectives = [obj for obj in objectives if obj.get('done')]
    pending_objectives = [obj for obj in objectives if not obj.get('done')]
    
    # Extraire les goals depuis user_data
    personal_goals = user_data.get('personal_goals', [])
    print(user_data)
    print(personal_goals)

    goals_text = "\n".join([f"- {goal.get('title')}: {goal.get('description')} ==> completed: {goal.get('completed')}" 
                           for goal in personal_goals]) if personal_goals else "No personal goals set"

    print(goals_text)
    prompt = f"""
    My Profile is :
    - Name: {user_data.get('name')}
    - Gender: {user_data.get('sexe')}
    - Height: {user_data.get('taille')} cm
    - Weight: {user_data.get('poids')} kg
    - Level: {user_data.get('niveau')}
    - Health Condition: {user_data.get('healthCondition')}

    Personal Goals:
    {goals_text}

    Completed Objectives:
    {[f"- {obj.get('nom')} ({obj.get('type')}): {obj.get('value')}" for obj in completed_objectives]}

    Pending Objectives:
    {[f"- {obj.get('nom')} ({obj.get('type')}): {obj.get('value')}" for obj in pending_objectives]}

    Based on this information:
    1. Consider the user's personal goals when making recommendations
    2. Analyze current progress and suggest adjustments for pending objectives
    3. Provide personalized recommendations based on completed objectives
    4. Consider fitness level, health condition, and preferences

    give me Respond concisely and structured, without special formatting (don't forget that you talk to me)
    """
    return prompt




## *********************************************************************** ##
def create_exercise_selection_prompt(user_level, health_condition, completed_exercises, exercises):
    exercise_list = "\n".join([f"- {ex['nom']} ({ex['niveau']}, {ex['type']})" for ex in exercises])
    return f"""
    From these exercises:
    {exercise_list}
    
    Select 5 most suitable exercises for a {user_level} level user with {health_condition} health condition.
    Completed exercises to exclude: {completed_exercises}
    Consider:
    1. User's level and health limitations
    2. Exercise variety and progression
    3. Balance between different exercise types
    
    Return only the exercise names, separated by commas.
    """




## *********************************************************************** ##
def get_recommended_exercises(user_level, completed_exercises, health_condition):
    try:
        prompt = create_exercise_selection_prompt(user_level, health_condition, completed_exercises, exercises)
        model = genai.GenerativeModel('gemini-1.5-flash')
        response = model.generate_content(prompt)
        
        recommended_names = [name.strip().lower() for name in response.text.split(',')]
        print(recommended_names)
        recommended_exercises = [
            ex for ex in exercises 
            if ex['nom'].lower() in recommended_names 
            and ex['nom'] not in completed_exercises
        ]
        print(recommended_exercises)
        
        return sorted(recommended_exercises, key=lambda x: x['id'])[:5]
    except Exception as e:
        # Fallback to original logic
        suitable_exercises = [ex for ex in exercises if ex['niveau'] == user_level]
        new_exercises = [ex for ex in suitable_exercises if ex['nom'] not in completed_exercises]
        return sorted(new_exercises, key=lambda x: x['id'])[:5]



## *********************************************************************** ##
def clean_response_text(text):
    cleaned_text = re.sub(r'[\*\#\@\!\%\&\(\)\[\]\{\}\<\>\+\=\-]', '', text)
    cleaned_text = re.sub(r'\s+', ' ', cleaned_text)
    return cleaned_text.strip()




## *********************************************************************** ##
def generate_recommendations(user_data, objectives):
    try:
        prompt = create_personalized_prompt(user_data, objectives)
        model = genai.GenerativeModel('gemini-1.5-flash', generation_config={"response_mime_type": "text/plain"})
        response = model.generate_content(prompt)
        text_recommendations = clean_response_text(response.text)
        
        personalized_response = f"Hi {user_data.get('name')}! Here are your personalized recommendations:\n\n{text_recommendations}"

        completed_exercises = [obj['nom'] for obj in objectives if obj.get('done')]
        
        recommended_exercises = get_recommended_exercises(
            user_data.get('niveau', 'beginner'),
            completed_exercises,
            user_data.get('healthCondition')
        )
        
        return {
            "textual_recommendations": personalized_response,
            "exercise_recommendations": recommended_exercises
        }
    except Exception as e:
        print(f"Error generating recommendations: {str(e)}")
        return {"error": f"Error generating recommendations: {str(e)}"}







## *********************************************************************** ##
@app.route("/api/fitness", methods=['POST'])
def fitness_assistant():
    data = request.json
    if not data or 'user_data' not in data or 'objectives' not in data:
        return jsonify({"error": "Missing user data or objectives"}), 400

    try:
        recommendations = generate_recommendations(data['user_data'], data['objectives'])
        return jsonify(recommendations)
    except Exception as e:
        return jsonify({"error": f"Processing error: {str(e)}"}), 500





## *********************************************************************** ##
@app.route("/api/exercises/recommended", methods=['POST'])
def get_recommended_exercises_route():
    data = request.json
    if not data or 'user_data' not in data or 'completed_exercises' not in data:
        return jsonify({"error": "Missing user data or completed exercises"}), 400

    try:
        recommended = get_recommended_exercises(
            data['user_data'].get('niveau', 'beginner'),
            data['completed_exercises'],
            data['user_data'].get('healthCondition')
        )
        return jsonify({"recommended_exercises": recommended})
    except Exception as e:
        return jsonify({"error": f"Processing error: {str(e)}"}), 500





## *********************************************************************** ##
if __name__ == '__main__':
    app.run(debug=True)