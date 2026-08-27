import 'package:flutter/material.dart';

import '../models/linkedin_profile.dart';

class PostsSection extends StatelessWidget {
  final List<HybridPost> posts;

  const PostsSection({super.key, required this.posts});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Posts', style: Theme.of(context).textTheme.titleMedium),
            const SizedBox(height: 8),
            if (posts.isEmpty)
              const Text('null')
            else
              ...posts.asMap().entries.map(
                    (entry) => Padding(
                      padding: const EdgeInsets.only(bottom: 12),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            'Post ${entry.key + 1}',
                            style: Theme.of(context).textTheme.bodyLarge,
                          ),
                          const SizedBox(height: 4),
                          _field('Date Published', entry.value.datePublished),
                          _field(
                              'Like Count', entry.value.likeCount?.toString()),
                          _field('Post URL', entry.value.postUrl),
                          _field('Text', entry.value.text),
                        ],
                      ),
                    ),
                  ),
          ],
        ),
      ),
    );
  }

  Widget _field(String label, String? value) {
    final displayValue = value ?? 'null';
    return Padding(
      padding: const EdgeInsets.only(bottom: 4),
      child: RichText(
        text: TextSpan(
          style: const TextStyle(color: Colors.black87),
          children: [
            TextSpan(
              text: '$label: ',
              style: const TextStyle(fontWeight: FontWeight.w600),
            ),
            TextSpan(text: displayValue),
          ],
        ),
      ),
    );
  }
}
