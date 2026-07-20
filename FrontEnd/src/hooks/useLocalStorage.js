// src/hooks/useLocalStorage.js
import { useState, useEffect } from 'react';

export function useLocalStorage(key, initialValue) {
    const [storedValue, setStoredValue] = useState(() => {
        try {
            const item = window.localStorage.getItem(key);
            // ⭐ Nếu localStorage đã có data → dùng data đó (KHÔNG ghi đè)
            if (item) return JSON.parse(item);
            // ⭐ Nếu chưa có → ghi initialValue vào localStorage
            window.localStorage.setItem(key, JSON.stringify(initialValue));
            return initialValue;
        } catch (error) {
            console.error('Lỗi khi đọc localStorage:', error);
            return initialValue;
        }
    });

    useEffect(() => {
        try {
            window.localStorage.setItem(key, JSON.stringify(storedValue));
        } catch (error) {
            console.error('Lỗi khi lưu localStorage:', error);
        }
    }, [key, storedValue]);

    useEffect(() => {
        const handleStorageChange = (e) => {
            if (e.key === key && e.newValue) {
                setStoredValue(JSON.parse(e.newValue));
            }
        };
        window.addEventListener('storage', handleStorageChange);
        return () => {
            window.removeEventListener('storage', handleStorageChange);
        };
    }, [key]);

    return [storedValue, setStoredValue];
}