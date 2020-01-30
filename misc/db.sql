--
-- PostgreSQL database dump
--

-- Dumped from database version 11.5
-- Dumped by pg_dump version 11.5

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: orders
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO orders;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: link; Type: TABLE; Schema: public; Owner: orders
--

CREATE TABLE public.link (
    id bigint NOT NULL,
    description character varying(255),
    href character varying(255)
);


ALTER TABLE public.link OWNER TO orders;

--
-- Name: media; Type: TABLE; Schema: public; Owner: orders
--

CREATE TABLE public.media (
    id bigint NOT NULL,
    description character varying(255),
    extension character varying(255),
    file_name character varying(255),
    href character varying(255),
    type character varying(255)
);


ALTER TABLE public.media OWNER TO orders;

--
-- Name: podcast; Type: TABLE; Schema: public; Owner: orders
--

CREATE TABLE public.podcast (
    id bigint NOT NULL,
    date timestamp without time zone,
    description character varying(255),
    notes character varying(255),
    podbean_draft_created timestamp without time zone,
    podbean_draft_published timestamp without time zone,
    podbean_media_uri character varying(255),
    s3_audio_file_name character varying(255),
    s3_audio_uri character varying(255),
    s3_photo_file_name character varying(255),
    s3_photo_uri character varying(255),
    title character varying(255),
    transcript character varying(255),
    uid character varying(255),
    podbean_photo_uri character varying(255)
);


ALTER TABLE public.podcast OWNER TO orders;

--
-- Name: podcast_link; Type: TABLE; Schema: public; Owner: orders
--

CREATE TABLE public.podcast_link (
    podcast_id bigint NOT NULL,
    link_id bigint NOT NULL
);


ALTER TABLE public.podcast_link OWNER TO orders;

--
-- Name: podcast_media; Type: TABLE; Schema: public; Owner: orders
--

CREATE TABLE public.podcast_media (
    podcast_id bigint NOT NULL,
    media_id bigint NOT NULL
);


ALTER TABLE public.podcast_media OWNER TO orders;

--
-- Data for Name: link; Type: TABLE DATA; Schema: public; Owner: orders
--

COPY public.link (id, description, href) FROM stdin;
\.


--
-- Data for Name: media; Type: TABLE DATA; Schema: public; Owner: orders
--

COPY public.media (id, description, extension, file_name, href, type) FROM stdin;
250	\N	mp3	interview.mp3	s3://podcast-input-bucket/91f7d3b1-d724-4c2c-b2b9-a750420470dc/interview.mp3	interview
251	\N	mp3	intro.mp3	s3://podcast-input-bucket/91f7d3b1-d724-4c2c-b2b9-a750420470dc/intro.mp3	introduction
252	\N	jpg	juergen-and-i.jpg	s3://podcast-input-bucket/91f7d3b1-d724-4c2c-b2b9-a750420470dc/juergen-and-i.jpg	photo
254	\N	mp3	interview.mp3	s3://podcast-input-bucket/628ff672-9f2c-43fb-b2a5-d5d2ad86117a/interview.mp3	interview
255	\N	mp3	intro.mp3	s3://podcast-input-bucket/628ff672-9f2c-43fb-b2a5-d5d2ad86117a/intro.mp3	introduction
256	\N	jpg	4.jpg	s3://podcast-input-bucket/628ff672-9f2c-43fb-b2a5-d5d2ad86117a/4.jpg	photo
246	\N	mp3	interview.mp3	s3://podcast-input-bucket/1b11d0a8-3646-439e-8024-5ac4fc39bf07/interview.mp3	interview
247	\N	mp3	intro.mp3	s3://podcast-input-bucket/1b11d0a8-3646-439e-8024-5ac4fc39bf07/intro.mp3	introduction
248	\N	jpg	dave-and-i.jpg	s3://podcast-input-bucket/1b11d0a8-3646-439e-8024-5ac4fc39bf07/dave-and-i.jpg	photo
\.


--
-- Data for Name: podcast; Type: TABLE DATA; Schema: public; Owner: orders
--

COPY public.podcast (id, date, description, notes, podbean_draft_created, podbean_draft_published, podbean_media_uri, s3_audio_file_name, s3_audio_uri, s3_photo_file_name, s3_photo_uri, title, transcript, uid, podbean_photo_uri) FROM stdin;
249	2019-10-19 10:23:54	take 3	\N	2020-01-25 15:50:49.13	\N	https://starbuxman.podbean.com/mf/play/3u83m7/91f7d3b1-d724-4c2c-b2b9-a750420470dc.mp3	91f7d3b1-d724-4c2c-b2b9-a750420470dc.mp3	s3://podcast-output-bucket/91f7d3b1-d724-4c2c-b2b9-a750420470dc.mp3	juergen-and-i.jpg	s3://podcast-input-bucket/91f7d3b1-d724-4c2c-b2b9-a750420470dc/juergen-and-i.jpg	take 3	\N	91f7d3b1-d724-4c2c-b2b9-a750420470dc	https://mcdn.podbean.com/mf/web/f8e4gm/91f7d3b1-d724-4c2c-b2b9-a750420470dc.jpg
245	2020-02-02 10:23:54	Josh Long (@starbuxman) talks to another amazing guest @ 01/25/2020 15:26:41.385	\N	2020-01-25 15:33:16.535	\N	https://starbuxman.podbean.com/mf/play/kquy5w/1b11d0a8-3646-439e-8024-5ac4fc39bf07.mp3	1b11d0a8-3646-439e-8024-5ac4fc39bf07.mp3	s3://podcast-output-bucket/1b11d0a8-3646-439e-8024-5ac4fc39bf07.mp3	dave-and-i.jpg	s3://podcast-input-bucket/1b11d0a8-3646-439e-8024-5ac4fc39bf07/dave-and-i.jpg	Josh Long (@starbuxman) talks to another amazing guest @ 01/25/2020 15:26:41.385	\N	1b11d0a8-3646-439e-8024-5ac4fc39bf07	https://mcdn.podbean.com/mf/web/uxbany/1b11d0a8-3646-439e-8024-5ac4fc39bf07.jpg
253	2020-05-02 13:55:54	another test	\N	2020-01-25 16:45:05.626	\N	https://starbuxman.podbean.com/mf/play/paggfc/628ff672-9f2c-43fb-b2a5-d5d2ad86117a.mp3	628ff672-9f2c-43fb-b2a5-d5d2ad86117a.mp3	s3://podcast-output-bucket/628ff672-9f2c-43fb-b2a5-d5d2ad86117a.mp3	4.jpg	s3://podcast-input-bucket/628ff672-9f2c-43fb-b2a5-d5d2ad86117a/4.jpg	Another test	\N	628ff672-9f2c-43fb-b2a5-d5d2ad86117a	https://mcdn.podbean.com/mf/web/j9vpz3/628ff672-9f2c-43fb-b2a5-d5d2ad86117a.jpg
\.


--
-- Data for Name: podcast_link; Type: TABLE DATA; Schema: public; Owner: orders
--

COPY public.podcast_link (podcast_id, link_id) FROM stdin;
\.


--
-- Data for Name: podcast_media; Type: TABLE DATA; Schema: public; Owner: orders
--

COPY public.podcast_media (podcast_id, media_id) FROM stdin;
245	246
245	247
245	248
249	250
249	251
249	252
253	254
253	255
253	256
\.


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: orders
--

SELECT pg_catalog.setval('public.hibernate_sequence', 256, true);


--
-- Name: link link_pkey; Type: CONSTRAINT; Schema: public; Owner: orders
--

ALTER TABLE ONLY public.link
    ADD CONSTRAINT link_pkey PRIMARY KEY (id);


--
-- Name: media media_pkey; Type: CONSTRAINT; Schema: public; Owner: orders
--

ALTER TABLE ONLY public.media
    ADD CONSTRAINT media_pkey PRIMARY KEY (id);


--
-- Name: podcast podcast_pkey; Type: CONSTRAINT; Schema: public; Owner: orders
--

ALTER TABLE ONLY public.podcast
    ADD CONSTRAINT podcast_pkey PRIMARY KEY (id);


--
-- Name: podcast_link fk2vu3w8tjdo0qb3vkpeydcc3w0; Type: FK CONSTRAINT; Schema: public; Owner: orders
--

ALTER TABLE ONLY public.podcast_link
    ADD CONSTRAINT fk2vu3w8tjdo0qb3vkpeydcc3w0 FOREIGN KEY (link_id) REFERENCES public.link(id);


--
-- Name: podcast_media fk8g18uypwfolj3nu8jew7vj6ex; Type: FK CONSTRAINT; Schema: public; Owner: orders
--

ALTER TABLE ONLY public.podcast_media
    ADD CONSTRAINT fk8g18uypwfolj3nu8jew7vj6ex FOREIGN KEY (media_id) REFERENCES public.media(id);


--
-- Name: podcast_media fkep89648nfax8u5t7cjle9bh77; Type: FK CONSTRAINT; Schema: public; Owner: orders
--

ALTER TABLE ONLY public.podcast_media
    ADD CONSTRAINT fkep89648nfax8u5t7cjle9bh77 FOREIGN KEY (podcast_id) REFERENCES public.podcast(id);


--
-- Name: podcast_link fkllcnm8ch4ses0kchqayiylv9c; Type: FK CONSTRAINT; Schema: public; Owner: orders
--

ALTER TABLE ONLY public.podcast_link
    ADD CONSTRAINT fkllcnm8ch4ses0kchqayiylv9c FOREIGN KEY (podcast_id) REFERENCES public.podcast(id);


--
-- PostgreSQL database dump complete
--

