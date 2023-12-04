#This program is going to start at a domain and crawl adjacent webpages

import requests
from bs4 import BeautifulSoup as bsoup
import csv
import re
import time

http = 'http://'
https = 'https://'

nodes = []
init_url = input("Enter Initial URL Here: ")
urls = [init_url]
visited = []

def crawl_page(soup, c_url):
    link_elements = soup.select('a[href]')
    for link_element in link_elements:
        url = link_element['href']

        if re.search(r"https://www.", url) and url not in visited:
            return


def get_html(url):
    try:
        return requests.get(url).content
    except Exception as e:
        print(e)
        return ''

while len(urls) != 0:
    time.sleep(1)
    current_url = urls.pop()
    visited.append(current_url)

    if len(urls) > 50:
        break

    if get_html(current_url) == '':
        continue
    response = requests.get(current_url)
    soup = bsoup(response.content, "html.parser")

    crawl_page(soup, current_url)

    link_elements = soup.select("a[href]")
    for link_element in link_elements:
        url = link_element['href']
        if url not in visited:
            if http in url or https in url:
                node = {"node1" : current_url, "node2" : url}
                nodes.append(node)
                urls.append(url)
                print(url)
    print(len(urls))

with open('nodes.csv', 'w') as csv_file:
    writer = csv.writer(csv_file)
    for node in nodes:
        writer.writerow(node.values())
