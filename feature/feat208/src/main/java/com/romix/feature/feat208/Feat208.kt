package com.romix.feature.feat208

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat208Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat208UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat208FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat208UserSummary
)

data class Feat208UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat208NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat208Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat208Config = Feat208Config()
) {

    fun loadSnapshot(userId: Long): Feat208NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat208NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat208UserSummary {
        return Feat208UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat208FeedItem> {
        val result = java.util.ArrayList<Feat208FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat208FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat208UiMapper {

    fun mapToUi(model: List<Feat208FeedItem>): Feat208UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat208UiModel(
            header = UiText("Feat208 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat208UiModel =
        Feat208UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat208UiModel =
        Feat208UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat208UiModel =
        Feat208UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat208Service(
    private val repository: Feat208Repository,
    private val uiMapper: Feat208UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat208UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat208UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat208UserItem1(val user: CoreUser, val label: String)
data class Feat208UserItem2(val user: CoreUser, val label: String)
data class Feat208UserItem3(val user: CoreUser, val label: String)
data class Feat208UserItem4(val user: CoreUser, val label: String)
data class Feat208UserItem5(val user: CoreUser, val label: String)
data class Feat208UserItem6(val user: CoreUser, val label: String)
data class Feat208UserItem7(val user: CoreUser, val label: String)
data class Feat208UserItem8(val user: CoreUser, val label: String)
data class Feat208UserItem9(val user: CoreUser, val label: String)
data class Feat208UserItem10(val user: CoreUser, val label: String)

data class Feat208StateBlock1(val state: Feat208UiModel, val checksum: Int)
data class Feat208StateBlock2(val state: Feat208UiModel, val checksum: Int)
data class Feat208StateBlock3(val state: Feat208UiModel, val checksum: Int)
data class Feat208StateBlock4(val state: Feat208UiModel, val checksum: Int)
data class Feat208StateBlock5(val state: Feat208UiModel, val checksum: Int)
data class Feat208StateBlock6(val state: Feat208UiModel, val checksum: Int)
data class Feat208StateBlock7(val state: Feat208UiModel, val checksum: Int)
data class Feat208StateBlock8(val state: Feat208UiModel, val checksum: Int)
data class Feat208StateBlock9(val state: Feat208UiModel, val checksum: Int)
data class Feat208StateBlock10(val state: Feat208UiModel, val checksum: Int)

fun buildFeat208UserItem(user: CoreUser, index: Int): Feat208UserItem1 {
    return Feat208UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat208StateBlock(model: Feat208UiModel): Feat208StateBlock1 {
    return Feat208StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat208UserSummary> {
    val list = java.util.ArrayList<Feat208UserSummary>(users.size)
    for (user in users) {
        list += Feat208UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat208UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat208UiModel {
    val summaries = (0 until count).map {
        Feat208UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat208UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat208UiModel> {
    val models = java.util.ArrayList<Feat208UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat208AnalyticsEvent1(val name: String, val value: String)
data class Feat208AnalyticsEvent2(val name: String, val value: String)
data class Feat208AnalyticsEvent3(val name: String, val value: String)
data class Feat208AnalyticsEvent4(val name: String, val value: String)
data class Feat208AnalyticsEvent5(val name: String, val value: String)
data class Feat208AnalyticsEvent6(val name: String, val value: String)
data class Feat208AnalyticsEvent7(val name: String, val value: String)
data class Feat208AnalyticsEvent8(val name: String, val value: String)
data class Feat208AnalyticsEvent9(val name: String, val value: String)
data class Feat208AnalyticsEvent10(val name: String, val value: String)

fun logFeat208Event1(event: Feat208AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat208Event2(event: Feat208AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat208Event3(event: Feat208AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat208Event4(event: Feat208AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat208Event5(event: Feat208AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat208Event6(event: Feat208AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat208Event7(event: Feat208AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat208Event8(event: Feat208AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat208Event9(event: Feat208AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat208Event10(event: Feat208AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat208Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat208Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat208Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat208Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat208Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat208Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat208Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat208Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat208Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat208Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat208(u: CoreUser): Feat208Projection1 =
    Feat208Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat208Projection1> {
    val list = java.util.ArrayList<Feat208Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat208(u)
    }
    return list
}
