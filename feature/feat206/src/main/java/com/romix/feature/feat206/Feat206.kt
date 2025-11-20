package com.romix.feature.feat206

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat206Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat206UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat206FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat206UserSummary
)

data class Feat206UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat206NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat206Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat206Config = Feat206Config()
) {

    fun loadSnapshot(userId: Long): Feat206NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat206NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat206UserSummary {
        return Feat206UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat206FeedItem> {
        val result = java.util.ArrayList<Feat206FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat206FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat206UiMapper {

    fun mapToUi(model: List<Feat206FeedItem>): Feat206UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat206UiModel(
            header = UiText("Feat206 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat206UiModel =
        Feat206UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat206UiModel =
        Feat206UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat206UiModel =
        Feat206UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat206Service(
    private val repository: Feat206Repository,
    private val uiMapper: Feat206UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat206UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat206UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat206UserItem1(val user: CoreUser, val label: String)
data class Feat206UserItem2(val user: CoreUser, val label: String)
data class Feat206UserItem3(val user: CoreUser, val label: String)
data class Feat206UserItem4(val user: CoreUser, val label: String)
data class Feat206UserItem5(val user: CoreUser, val label: String)
data class Feat206UserItem6(val user: CoreUser, val label: String)
data class Feat206UserItem7(val user: CoreUser, val label: String)
data class Feat206UserItem8(val user: CoreUser, val label: String)
data class Feat206UserItem9(val user: CoreUser, val label: String)
data class Feat206UserItem10(val user: CoreUser, val label: String)

data class Feat206StateBlock1(val state: Feat206UiModel, val checksum: Int)
data class Feat206StateBlock2(val state: Feat206UiModel, val checksum: Int)
data class Feat206StateBlock3(val state: Feat206UiModel, val checksum: Int)
data class Feat206StateBlock4(val state: Feat206UiModel, val checksum: Int)
data class Feat206StateBlock5(val state: Feat206UiModel, val checksum: Int)
data class Feat206StateBlock6(val state: Feat206UiModel, val checksum: Int)
data class Feat206StateBlock7(val state: Feat206UiModel, val checksum: Int)
data class Feat206StateBlock8(val state: Feat206UiModel, val checksum: Int)
data class Feat206StateBlock9(val state: Feat206UiModel, val checksum: Int)
data class Feat206StateBlock10(val state: Feat206UiModel, val checksum: Int)

fun buildFeat206UserItem(user: CoreUser, index: Int): Feat206UserItem1 {
    return Feat206UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat206StateBlock(model: Feat206UiModel): Feat206StateBlock1 {
    return Feat206StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat206UserSummary> {
    val list = java.util.ArrayList<Feat206UserSummary>(users.size)
    for (user in users) {
        list += Feat206UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat206UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat206UiModel {
    val summaries = (0 until count).map {
        Feat206UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat206UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat206UiModel> {
    val models = java.util.ArrayList<Feat206UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat206AnalyticsEvent1(val name: String, val value: String)
data class Feat206AnalyticsEvent2(val name: String, val value: String)
data class Feat206AnalyticsEvent3(val name: String, val value: String)
data class Feat206AnalyticsEvent4(val name: String, val value: String)
data class Feat206AnalyticsEvent5(val name: String, val value: String)
data class Feat206AnalyticsEvent6(val name: String, val value: String)
data class Feat206AnalyticsEvent7(val name: String, val value: String)
data class Feat206AnalyticsEvent8(val name: String, val value: String)
data class Feat206AnalyticsEvent9(val name: String, val value: String)
data class Feat206AnalyticsEvent10(val name: String, val value: String)

fun logFeat206Event1(event: Feat206AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat206Event2(event: Feat206AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat206Event3(event: Feat206AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat206Event4(event: Feat206AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat206Event5(event: Feat206AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat206Event6(event: Feat206AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat206Event7(event: Feat206AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat206Event8(event: Feat206AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat206Event9(event: Feat206AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat206Event10(event: Feat206AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat206Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat206Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat206Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat206Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat206Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat206Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat206Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat206Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat206Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat206Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat206(u: CoreUser): Feat206Projection1 =
    Feat206Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat206Projection1> {
    val list = java.util.ArrayList<Feat206Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat206(u)
    }
    return list
}
