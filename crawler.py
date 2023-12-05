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
    if 'gustavus.edu' not in current_url:
        continue
    visited.append(current_url)

    if len(nodes) >= 50000:
        break

    if get_html(current_url) == '':
        continue
    response = requests.get(current_url)
    soup = bsoup(response.content, "html.parser")

    crawl_page(soup, current_url)

    link_elements = soup.select("a[href]")
    check_dupes = []
    for link_element in link_elements:
        url = link_element['href']
        if url not in visited:
            if http in url or https in url:
                if url not in check_dupes:
                    node = {"node1" : current_url, "node2" : url}
                    nodes.append(node)
                    urls.append(url)
                    check_dupes.append(url)
                    print(url)

    print(len(urls))

with open('nodes.csv', 'w', newline = '') as csv_file:
    writer = csv.writer(csv_file)
    for node in nodes:
        writer.writerow(node.values())
