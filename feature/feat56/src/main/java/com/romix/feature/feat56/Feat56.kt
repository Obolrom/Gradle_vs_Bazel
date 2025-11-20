package com.romix.feature.feat56

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat56Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat56UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat56FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat56UserSummary
)

data class Feat56UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat56NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat56Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat56Config = Feat56Config()
) {

    fun loadSnapshot(userId: Long): Feat56NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat56NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat56UserSummary {
        return Feat56UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat56FeedItem> {
        val result = java.util.ArrayList<Feat56FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat56FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat56UiMapper {

    fun mapToUi(model: List<Feat56FeedItem>): Feat56UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat56UiModel(
            header = UiText("Feat56 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat56UiModel =
        Feat56UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat56UiModel =
        Feat56UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat56UiModel =
        Feat56UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat56Service(
    private val repository: Feat56Repository,
    private val uiMapper: Feat56UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat56UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat56UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat56UserItem1(val user: CoreUser, val label: String)
data class Feat56UserItem2(val user: CoreUser, val label: String)
data class Feat56UserItem3(val user: CoreUser, val label: String)
data class Feat56UserItem4(val user: CoreUser, val label: String)
data class Feat56UserItem5(val user: CoreUser, val label: String)
data class Feat56UserItem6(val user: CoreUser, val label: String)
data class Feat56UserItem7(val user: CoreUser, val label: String)
data class Feat56UserItem8(val user: CoreUser, val label: String)
data class Feat56UserItem9(val user: CoreUser, val label: String)
data class Feat56UserItem10(val user: CoreUser, val label: String)

data class Feat56StateBlock1(val state: Feat56UiModel, val checksum: Int)
data class Feat56StateBlock2(val state: Feat56UiModel, val checksum: Int)
data class Feat56StateBlock3(val state: Feat56UiModel, val checksum: Int)
data class Feat56StateBlock4(val state: Feat56UiModel, val checksum: Int)
data class Feat56StateBlock5(val state: Feat56UiModel, val checksum: Int)
data class Feat56StateBlock6(val state: Feat56UiModel, val checksum: Int)
data class Feat56StateBlock7(val state: Feat56UiModel, val checksum: Int)
data class Feat56StateBlock8(val state: Feat56UiModel, val checksum: Int)
data class Feat56StateBlock9(val state: Feat56UiModel, val checksum: Int)
data class Feat56StateBlock10(val state: Feat56UiModel, val checksum: Int)

fun buildFeat56UserItem(user: CoreUser, index: Int): Feat56UserItem1 {
    return Feat56UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat56StateBlock(model: Feat56UiModel): Feat56StateBlock1 {
    return Feat56StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat56UserSummary> {
    val list = java.util.ArrayList<Feat56UserSummary>(users.size)
    for (user in users) {
        list += Feat56UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat56UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat56UiModel {
    val summaries = (0 until count).map {
        Feat56UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat56UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat56UiModel> {
    val models = java.util.ArrayList<Feat56UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat56AnalyticsEvent1(val name: String, val value: String)
data class Feat56AnalyticsEvent2(val name: String, val value: String)
data class Feat56AnalyticsEvent3(val name: String, val value: String)
data class Feat56AnalyticsEvent4(val name: String, val value: String)
data class Feat56AnalyticsEvent5(val name: String, val value: String)
data class Feat56AnalyticsEvent6(val name: String, val value: String)
data class Feat56AnalyticsEvent7(val name: String, val value: String)
data class Feat56AnalyticsEvent8(val name: String, val value: String)
data class Feat56AnalyticsEvent9(val name: String, val value: String)
data class Feat56AnalyticsEvent10(val name: String, val value: String)

fun logFeat56Event1(event: Feat56AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat56Event2(event: Feat56AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat56Event3(event: Feat56AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat56Event4(event: Feat56AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat56Event5(event: Feat56AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat56Event6(event: Feat56AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat56Event7(event: Feat56AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat56Event8(event: Feat56AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat56Event9(event: Feat56AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat56Event10(event: Feat56AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat56Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat56Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat56Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat56Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat56Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat56Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat56Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat56Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat56Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat56Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat56(u: CoreUser): Feat56Projection1 =
    Feat56Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat56Projection1> {
    val list = java.util.ArrayList<Feat56Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat56(u)
    }
    return list
}
