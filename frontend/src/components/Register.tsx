import React, { useState, type FormEvent } from "react";
import { register } from "../services/api";
import { useNavigate } from "react-router-dom";
import passwordBg from '../assets/password-bg.jpg';

const Register: React.FC = () => {
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    phone: "",
    password: "",
    confirmPassword: "",
  });
  const [errors, setErrors] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const validateForm = () => {
    const newErrors: string[] = [];
    if (formData.username.length < 3 || formData.username.length > 20) {
      newErrors.push("Kullanıcı adı 3-20 karakter olmalı.");
    }
    if (formData.phone.length < 10 || formData.phone.length > 15) {
      newErrors.push("Telefon numarası 10-15 karakter olmalı.");
    }
    if (formData.password.length < 8) {
      newErrors.push("Şifre en az 8 karakter olmalı.");
    }
    if (formData.password !== formData.confirmPassword) {
      newErrors.push("Şifreler eşleşmiyor.");
    }
    if (!formData.email.includes("@")) {
      newErrors.push("Geçerli bir e-posta adresi girin.");
    }
    return newErrors;
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setErrors([]);
    setLoading(true);

    const clientErrors = validateForm();
    if (clientErrors.length > 0) {
      setErrors(clientErrors);
      setLoading(false);
      return;
    }

    try {
      await register(formData);
      navigate("/login");
    } catch (err: any) {
      console.error("Hata detayları:", err.message, err);
      if (err.message === "Ağ hatası: Sunucuya bağlanılamadı.") {
        setErrors(["Daha sonra tekrar deneyiniz."]);
      } else if (err.message.includes(";")) {
        setErrors(err.message.split(";"));
      } else {
        setErrors([err.message || "Kayıt sırasında bir hata oluştu."]);
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-end relative overflow-hidden">
      {/* Arka plan görseli */}
      <div className="absolute inset-0 w-full h-full">
        <img src={passwordBg} alt="Password BG" className="w-full h-full object-cover filter-none brightness-100 contrast-100" />
      </div>

      <div className="w-full max-w-xl relative z-20 px-8 mr-32">
        {/* Logo ve başlık */}
        <div className="text-center mb-12">
          <div className="inline-block p-6 rounded-2xl bg-black/40 backdrop-blur-md mb-6 border border-white/10">
            <div className="relative">
              <span className="text-5xl font-black tracking-widest text-transparent bg-clip-text bg-gradient-to-r from-red-500 via-white to-red-500 animate-gradient-x">
                TRINITY
              </span>
              <div className="absolute -inset-1 bg-gradient-to-r from-red-500/20 via-white/20 to-red-500/20 blur-sm"></div>
            </div>
          </div>
          <h1 className="text-3xl font-bold mb-6 text-gray-300 animate-fade-in">Kayıt Ol</h1>
          <p className="text-gray-300">TRINITY'e Katılın</p>
        </div>

        {/* Form container */}
        <div className="relative group">
          {/* Animasyonlu border */}
          <div className="absolute -inset-0.5 bg-gradient-to-r from-black via-red-900 to-black rounded-3xl blur opacity-75 group-hover:opacity-100 transition duration-1000 group-hover:duration-200 animate-gradient"></div>
          
          <div className="relative bg-white/10 backdrop-blur-md rounded-3xl p-8 shadow-2xl border border-white/20 animate-fade-in">
            {errors.length > 0 && (
              <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-2 rounded mb-4 animate-fade-in">
                <ul className="list-disc list-inside">
                  {errors.map((err, index) => (
                    <li key={index}>{err}</li>
                  ))}
                </ul>
              </div>
            )}
            <form onSubmit={handleSubmit} className="space-y-6">
              <div>
                <div className="relative">
                  <input
                    type="text"
                    id="username"
                    name="username"
                    value={formData.username}
                    onChange={handleInputChange}
                    className="w-full pl-12 pr-4 py-4 bg-white/5 border border-white/10 rounded-xl text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-white/20 focus:border-transparent transition-all duration-300"
                    placeholder="Kullanıcı adı"
                    required
                  />
                  <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                    <svg className="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                    </svg>
                  </div>
                </div>
              </div>

              <div>
                <div className="relative">
                  <input
                    type="email"
                    id="email"
                    name="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    className="w-full pl-12 pr-4 py-4 bg-white/5 border border-white/10 rounded-xl text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-white/20 focus:border-transparent transition-all duration-300"
                    placeholder="E-posta"
                    required
                  />
                  <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                    <svg className="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                    </svg>
                  </div>
                </div>
              </div>

              <div>
                <div className="relative">
                  <input
                    type="tel"
                    id="phone"
                    name="phone"
                    value={formData.phone}
                    onChange={handleInputChange}
                    className="w-full pl-12 pr-4 py-4 bg-white/5 border border-white/10 rounded-xl text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-white/20 focus:border-transparent transition-all duration-300"
                    placeholder="Telefon Numarası"
                    required
                  />
                  <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                    <svg className="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                    </svg>
                  </div>
                </div>
              </div>

              <div>
                <div className="relative">
                  <input
                    type="password"
                    id="password"
                    name="password"
                    value={formData.password}
                    onChange={handleInputChange}
                    className="w-full pl-12 pr-4 py-4 bg-white/5 border border-white/10 rounded-xl text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-white/20 focus:border-transparent transition-all duration-300"
                    placeholder="Şifre"
                    required
                  />
                  <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                    <svg className="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                    </svg>
                  </div>
                </div>
              </div>

              <div>
                <div className="relative">
                  <input
                    type="password"
                    id="confirmPassword"
                    name="confirmPassword"
                    value={formData.confirmPassword}
                    onChange={handleInputChange}
                    className="w-full pl-12 pr-4 py-4 bg-white/5 border border-white/10 rounded-xl text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-white/20 focus:border-transparent transition-all duration-300"
                    placeholder="Şifre tekrar"
                    required
                  />
                  <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                    <svg className="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                    </svg>
                  </div>
                </div>
              </div>

              <button
                type="submit"
                disabled={loading}
                className={`w-full py-4 bg-gradient-to-r from-black to-red-900 text-white rounded-xl font-semibold hover:from-red-900 hover:to-black focus:outline-none focus:ring-2 focus:ring-white/20 focus:ring-offset-2 focus:ring-offset-gray-900 transform transition-all duration-300 hover:scale-[1.02] ${
                  loading ? "opacity-50 cursor-not-allowed" : ""
                }`}
              >
                {loading ? (
                  <div className="flex items-center justify-center">
                    <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    Yükleniyor...
                  </div>
                ) : (
                  "Kayıt Ol"
                )}
              </button>
            </form>

            <div className="mt-8 space-y-4 text-center">
              <a href="/login" className="block text-gray-300 hover:text-white transition-colors duration-200">
                Zaten hesabınız var mı? <span className="font-semibold">Giriş Yap</span>
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Register;