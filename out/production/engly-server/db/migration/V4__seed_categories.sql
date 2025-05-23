INSERT INTO categories (id, created_at, description, name, updated_at)
VALUES
    (gen_random_uuid(), now(), 'Open discussions about anything', 'GENERAL_CHAT', now()),
    (gen_random_uuid(), now(), 'Latest updates and discussions', 'NEWS', now()),
    (gen_random_uuid(), now(), 'Sports news and discussions', 'SPORTS', now()),
    (gen_random_uuid(), now(), 'Adventures, Culinary Delights, and Gastronomic Explorations', 'TRAVEL_AND_FOOD', now()),
    (gen_random_uuid(), now(), 'Innovations, Gadgets, and Future Trends', 'TECH', now()),
    (gen_random_uuid(), now(), 'Passions, Pastimes, and Creative Pursuits', 'HOBBIES', now()),
    (gen_random_uuid(), now(), 'Growth, Opportunities, and Professional Insights', 'CAREER', now()),
    (gen_random_uuid(), now(), 'Reviews, Discussions, and Cinematic Experiences', 'MOVIES', now())
ON CONFLICT (name) DO NOTHING;