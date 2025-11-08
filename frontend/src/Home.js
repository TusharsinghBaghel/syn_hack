// src/Home.js
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import Navbar from "./components/Navbar";
import Sidebar from "./components/Sidebar";
import QuestionsList from "./components/QuestionsList";
import PostQuestionModal from "./components/PostQuestionModal";
import "./Home.css";

const API_BASE = "http://localhost:3000";

function Home() {
  const [questions, setQuestions] = useState([]);
  const [myQuestions, setMyQuestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [selectedQuestion, setSelectedQuestion] = useState(null);
  const navigate = useNavigate();

  const token = localStorage.getItem("token");

  // Fetch all questions
  const fetchQuestions = async () => {
    try {
      setLoading(true);
      const response = await axios.get(`${API_BASE}/questions`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setQuestions(response.data.questions);
      setLoading(false);
    } catch (err) {
      console.error(err);
      setLoading(false);
    }
  };

  // Fetch user's own questions
  const fetchMyQuestions = async () => {
    try {
      const response = await axios.get(`${API_BASE}/questions/my`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setMyQuestions(response.data.questions);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    if (!token) {
      navigate("/");
      return;
    }
    fetchQuestions();
    fetchMyQuestions();
  }, [token, navigate]);

  const handleRandomQuestion = () => {
    if (questions.length > 0) {
      const randomIndex = Math.floor(Math.random() * questions.length);
      navigate(`/question/${questions[randomIndex]._id}`);
    }
  };

  const handleQuestionClick = (questionId) => {
    navigate(`/question/${questionId}`);
  };

  const handlePostSuccess = () => {
    setShowModal(false);
    fetchQuestions();
    fetchMyQuestions();
  };

  return (
    <div className="home-wrapper">
      <Navbar
        onRandomQuestion={handleRandomQuestion}
        onPostQuestion={() => setShowModal(true)}
      />
      
      <div className="home-layout">
        <Sidebar
          myQuestions={myQuestions}
          onQuestionClick={handleQuestionClick}
        />
        
        <QuestionsList
          questions={questions}
          loading={loading}
          onQuestionClick={handleQuestionClick}
        />
      </div>

      {showModal && (
        <PostQuestionModal
          onClose={() => setShowModal(false)}
          onSuccess={handlePostSuccess}
          token={token}
        />
      )}
    </div>
  );
}

export default Home;