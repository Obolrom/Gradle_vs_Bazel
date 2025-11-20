package com.romix.feature.feat505

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat505Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat505UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat505FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat505UserSummary
)

data class Feat505UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat505NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat505Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat505Config = Feat505Config()
) {

    fun loadSnapshot(userId: Long): Feat505NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat505NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat505UserSummary {
        return Feat505UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat505FeedItem> {
        val result = java.util.ArrayList<Feat505FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat505FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat505UiMapper {

    fun mapToUi(model: List<Feat505FeedItem>): Feat505UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat505UiModel(
            header = UiText("Feat505 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat505UiModel =
        Feat505UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat505UiModel =
        Feat505UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat505UiModel =
        Feat505UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat505Service(
    private val repository: Feat505Repository,
    private val uiMapper: Feat505UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat505UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat505UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat505UserItem1(val user: CoreUser, val label: String)
data class Feat505UserItem2(val user: CoreUser, val label: String)
data class Feat505UserItem3(val user: CoreUser, val label: String)
data class Feat505UserItem4(val user: CoreUser, val label: String)
data class Feat505UserItem5(val user: CoreUser, val label: String)
data class Feat505UserItem6(val user: CoreUser, val label: String)
data class Feat505UserItem7(val user: CoreUser, val label: String)
data class Feat505UserItem8(val user: CoreUser, val label: String)
data class Feat505UserItem9(val user: CoreUser, val label: String)
data class Feat505UserItem10(val user: CoreUser, val label: String)

data class Feat505StateBlock1(val state: Feat505UiModel, val checksum: Int)
data class Feat505StateBlock2(val state: Feat505UiModel, val checksum: Int)
data class Feat505StateBlock3(val state: Feat505UiModel, val checksum: Int)
data class Feat505StateBlock4(val state: Feat505UiModel, val checksum: Int)
data class Feat505StateBlock5(val state: Feat505UiModel, val checksum: Int)
data class Feat505StateBlock6(val state: Feat505UiModel, val checksum: Int)
data class Feat505StateBlock7(val state: Feat505UiModel, val checksum: Int)
data class Feat505StateBlock8(val state: Feat505UiModel, val checksum: Int)
data class Feat505StateBlock9(val state: Feat505UiModel, val checksum: Int)
data class Feat505StateBlock10(val state: Feat505UiModel, val checksum: Int)

fun buildFeat505UserItem(user: CoreUser, index: Int): Feat505UserItem1 {
    return Feat505UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat505StateBlock(model: Feat505UiModel): Feat505StateBlock1 {
    return Feat505StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat505UserSummary> {
    val list = java.util.ArrayList<Feat505UserSummary>(users.size)
    for (user in users) {
        list += Feat505UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat505UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat505UiModel {
    val summaries = (0 until count).map {
        Feat505UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat505UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat505UiModel> {
    val models = java.util.ArrayList<Feat505UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat505AnalyticsEvent1(val name: String, val value: String)
data class Feat505AnalyticsEvent2(val name: String, val value: String)
data class Feat505AnalyticsEvent3(val name: String, val value: String)
data class Feat505AnalyticsEvent4(val name: String, val value: String)
data class Feat505AnalyticsEvent5(val name: String, val value: String)
data class Feat505AnalyticsEvent6(val name: String, val value: String)
data class Feat505AnalyticsEvent7(val name: String, val value: String)
data class Feat505AnalyticsEvent8(val name: String, val value: String)
data class Feat505AnalyticsEvent9(val name: String, val value: String)
data class Feat505AnalyticsEvent10(val name: String, val value: String)

fun logFeat505Event1(event: Feat505AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat505Event2(event: Feat505AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat505Event3(event: Feat505AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat505Event4(event: Feat505AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat505Event5(event: Feat505AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat505Event6(event: Feat505AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat505Event7(event: Feat505AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat505Event8(event: Feat505AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat505Event9(event: Feat505AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat505Event10(event: Feat505AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat505Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat505Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat505Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat505Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat505Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat505Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat505Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat505Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat505Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat505Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat505(u: CoreUser): Feat505Projection1 =
    Feat505Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat505Projection1> {
    val list = java.util.ArrayList<Feat505Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat505(u)
    }
    return list
}
