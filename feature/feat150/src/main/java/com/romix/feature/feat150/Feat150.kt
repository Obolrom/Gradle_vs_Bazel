package com.romix.feature.feat150

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat150Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat150UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat150FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat150UserSummary
)

data class Feat150UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat150NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat150Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat150Config = Feat150Config()
) {

    fun loadSnapshot(userId: Long): Feat150NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat150NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat150UserSummary {
        return Feat150UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat150FeedItem> {
        val result = java.util.ArrayList<Feat150FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat150FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat150UiMapper {

    fun mapToUi(model: List<Feat150FeedItem>): Feat150UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat150UiModel(
            header = UiText("Feat150 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat150UiModel =
        Feat150UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat150UiModel =
        Feat150UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat150UiModel =
        Feat150UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat150Service(
    private val repository: Feat150Repository,
    private val uiMapper: Feat150UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat150UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat150UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat150UserItem1(val user: CoreUser, val label: String)
data class Feat150UserItem2(val user: CoreUser, val label: String)
data class Feat150UserItem3(val user: CoreUser, val label: String)
data class Feat150UserItem4(val user: CoreUser, val label: String)
data class Feat150UserItem5(val user: CoreUser, val label: String)
data class Feat150UserItem6(val user: CoreUser, val label: String)
data class Feat150UserItem7(val user: CoreUser, val label: String)
data class Feat150UserItem8(val user: CoreUser, val label: String)
data class Feat150UserItem9(val user: CoreUser, val label: String)
data class Feat150UserItem10(val user: CoreUser, val label: String)

data class Feat150StateBlock1(val state: Feat150UiModel, val checksum: Int)
data class Feat150StateBlock2(val state: Feat150UiModel, val checksum: Int)
data class Feat150StateBlock3(val state: Feat150UiModel, val checksum: Int)
data class Feat150StateBlock4(val state: Feat150UiModel, val checksum: Int)
data class Feat150StateBlock5(val state: Feat150UiModel, val checksum: Int)
data class Feat150StateBlock6(val state: Feat150UiModel, val checksum: Int)
data class Feat150StateBlock7(val state: Feat150UiModel, val checksum: Int)
data class Feat150StateBlock8(val state: Feat150UiModel, val checksum: Int)
data class Feat150StateBlock9(val state: Feat150UiModel, val checksum: Int)
data class Feat150StateBlock10(val state: Feat150UiModel, val checksum: Int)

fun buildFeat150UserItem(user: CoreUser, index: Int): Feat150UserItem1 {
    return Feat150UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat150StateBlock(model: Feat150UiModel): Feat150StateBlock1 {
    return Feat150StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat150UserSummary> {
    val list = java.util.ArrayList<Feat150UserSummary>(users.size)
    for (user in users) {
        list += Feat150UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat150UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat150UiModel {
    val summaries = (0 until count).map {
        Feat150UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat150UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat150UiModel> {
    val models = java.util.ArrayList<Feat150UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat150AnalyticsEvent1(val name: String, val value: String)
data class Feat150AnalyticsEvent2(val name: String, val value: String)
data class Feat150AnalyticsEvent3(val name: String, val value: String)
data class Feat150AnalyticsEvent4(val name: String, val value: String)
data class Feat150AnalyticsEvent5(val name: String, val value: String)
data class Feat150AnalyticsEvent6(val name: String, val value: String)
data class Feat150AnalyticsEvent7(val name: String, val value: String)
data class Feat150AnalyticsEvent8(val name: String, val value: String)
data class Feat150AnalyticsEvent9(val name: String, val value: String)
data class Feat150AnalyticsEvent10(val name: String, val value: String)

fun logFeat150Event1(event: Feat150AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat150Event2(event: Feat150AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat150Event3(event: Feat150AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat150Event4(event: Feat150AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat150Event5(event: Feat150AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat150Event6(event: Feat150AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat150Event7(event: Feat150AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat150Event8(event: Feat150AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat150Event9(event: Feat150AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat150Event10(event: Feat150AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat150Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat150Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat150Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat150Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat150Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat150Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat150Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat150Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat150Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat150Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat150(u: CoreUser): Feat150Projection1 =
    Feat150Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat150Projection1> {
    val list = java.util.ArrayList<Feat150Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat150(u)
    }
    return list
}
