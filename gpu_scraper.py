import requests
from bs4 import BeautifulSoup
import pandas as pd
import json
import time

def scrape_gpu_data():
    # TechPowerUp GPU veritabanı sayfasına git
    base_url = "https://www.techpowerup.com/gpu-specs/"
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
    }
    
    all_gpu_data = []
    page = 1
    
    try:
        while True:
            url = f"{base_url}?mfgr=NVIDIA&mobile=No&workstation=No&sort=name&page={page}"
            print(f"Scraping page {page}...")
            
            response = requests.get(url, headers=headers)
            if response.status_code != 200:
                break
                
            soup = BeautifulSoup(response.text, 'html.parser')
            table = soup.find('table', id='gpu-specs')
            
            if not table:
                break
                
            rows = table.find_all('tr')[1:]  # Skip header row
            if not rows:
                break
                
            for row in rows:
                cols = row.find_all('td')
                if len(cols) >= 8:
                    name_link = cols[0].find('a')
                    if name_link:
                        # GPU detay sayfasından çıkış tarihini al
                        detail_url = "https://www.techpowerup.com" + name_link['href']
                        try:
                            detail_response = requests.get(detail_url, headers=headers)
                            detail_soup = BeautifulSoup(detail_response.text, 'html.parser')
                            release_date = ""
                            release_dt = detail_soup.find('dt', string='Release Date')
                            if release_dt:
                                release_dd = release_dt.find_next_sibling('dd')
                                if release_dd:
                                    release_date = release_dd.text.strip()
                        except:
                            release_date = ""
                            
                        time.sleep(1)  # Rate limiting
                    
                    gpu_data = {
                        "name": cols[0].text.strip(),
                        "architecture": cols[2].text.strip(),
                        "boost_clock": cols[4].text.strip(),
                        "base_clock": cols[3].text.strip(),
                        "memory_size": cols[5].text.strip(),
                        "memory_type": cols[6].text.strip(),
                        "tdp": cols[7].text.strip(),
                        "release_date": release_date
                    }
                    
                    all_gpu_data.append(gpu_data)
                    print(f"Scraped: {gpu_data['name']}")
            
            page += 1
            time.sleep(2)  # Rate limiting between pages
        
        if all_gpu_data:
            # Verileri DataFrame'e dönüştür
            df = pd.DataFrame(all_gpu_data)
            
            # CSV olarak kaydet
            df.to_csv("gpu_data.csv", index=False)
            
            # JSON olarak kaydet
            with open("gpu_data.json", "w", encoding="utf-8") as f:
                json.dump(all_gpu_data, f, ensure_ascii=False, indent=2)
            
            print("Data scraping completed successfully!")
            return all_gpu_data
        else:
            print("No data was scraped!")
            return None
            
    except Exception as e:
        print(f"An error occurred: {str(e)}")
        return None

if __name__ == "__main__":
    scrape_gpu_data() 